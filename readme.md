# Knowledge Base with PostgreSQL + pgvector

This project shows a practical RAG-style indexing and retrieval flow on top of Spring Boot, PostgreSQL, `pgvector`, and LangChain4j.

The current implementation supports this pipeline:

1. Create a knowledge document through a REST endpoint
2. Split the document into smaller chunks
3. Generate one embedding per chunk
4. Persist vectors in PostgreSQL using `pgvector`
5. Search with semantic similarity, optional keyword ranking, and exact-match metadata filters
6. Build a prompt from the retrieved chunks and send it through a LangChain4j chat model

LangChain4j is now part of the runtime path, but it sits behind application ports:

- `DocumentChunker` is the application port for chunking
- `EmbeddingPort` is the application port for vector generation
- `ChatPort` is the application port for answer generation
- LangChain4j adapters implement those ports
- prompt construction uses LangChain4j `PromptTemplate` internally, but returns a plain prompt string to the application layer

The project still ships with fake LangChain4j models so the workflow can run without provider credentials. Replacing them with a real provider is now a wiring task, not a structural rewrite.

## Architecture choices

The project is organized by business context first, not by technical layer:

- `document`: document ingestion, indexing lifecycle, chunk persistence, indexing events
- `search`: retrieval, prompt construction, answer generation flow
- `shared`: cross-cutting AI ports, LangChain4j adapters, and configuration

Inside each context, the code follows a hexagonal structure:

- `domain`: entities and domain state
- `application`: use cases, commands, queries, and outbound ports
- `adapter/in`: entry points such as REST controllers and event listeners
- `adapter/out`: implementations for persistence, event publication, chunking, and AI integrations

This means dependencies point inward:

- controllers depend on application services
- application services depend on ports
- adapters implement those ports
- domain classes do not depend on adapters

That structure was chosen to keep the core workflow stable while allowing infrastructure to change independently. Swapping PostgreSQL queries, replacing fake LangChain4j models, or moving from Spring events to another messaging mechanism should only affect adapter code.

### Why hexagonal here

This project has two infrastructure-heavy flows:

1. Document ingestion and indexing
2. Question answering over vector search

Both flows need external tools:

- HTTP for input
- PostgreSQL for storage and retrieval
- LangChain4j for chunking, embeddings, and chat
- Spring events for asynchronous-style orchestration

Without hexagonal boundaries, those concerns quickly leak into the use cases. The current structure keeps the use cases focused on orchestration and rules:

- `KnowledgeDocumentService` creates a document and publishes a domain-level application event
- `KnowledgeDocumentIndexer` performs chunking and embedding through ports
- `RetrievalService` embeds the question and delegates retrieval through a search port
- `SemanticSearchService` coordinates retrieval, prompt creation, and answer generation

### How SOLID influenced the design

- Single Responsibility Principle: controllers only translate HTTP, services only coordinate use cases, repositories only persist/query, adapters only integrate with external libraries.
- Open/Closed Principle: new AI providers or persistence strategies can be added by implementing ports instead of rewriting use cases.
- Liskov Substitution Principle: fake and real AI adapters implement the same `ChatPort` and `EmbeddingPort`, so tests and runtime wiring use the same contracts.
- Interface Segregation Principle: ports stay narrow, for example `KnowledgeChunkSearchPort` is separate from document persistence concerns.
- Dependency Inversion Principle: application services depend on abstractions such as `KnowledgeDocumentStore`, `KnowledgeChunkSearchPort`, `EmbeddingPort`, and `ChatPort`, not on Spring Data or LangChain4j types.

### Package layout

Main source layout:

```text
src/main/java/com/example/knowledgebase
├── document
│   ├── adapter
│   │   ├── in
│   │   │   ├── event
│   │   │   └── web
│   │   └── out
│   │       ├── chunking
│   │       ├── event
│   │       └── persistence
│   ├── application
│   │   ├── command
│   │   ├── event
│   │   └── port/out
│   └── domain
├── search
│   ├── adapter
│   │   ├── in/web
│   │   └── out/persistence
│   ├── application
│   │   ├── port/out
│   │   └── query
│   └── domain
└── shared
    ├── adapter
    │   ├── config
    │   └── out/ai/langchain4j
    └── ai
```

The test tree mirrors the production structure so the architectural intent stays visible:

- adapter tests verify translation and wiring at the edges
- application tests verify use-case behavior with mocks for ports
- shared adapter tests verify LangChain4j integration behavior

That is why tests are grouped under paths like:

- `src/test/java/.../document/application`
- `src/test/java/.../document/adapter/in/web`
- `src/test/java/.../search/application`
- `src/test/java/.../shared/adapter/out/ai/langchain4j`

## Requirements

- Java 21
- Maven 3.9+
- Docker / Docker Compose

## Running the project

Start PostgreSQL with `pgvector` enabled:

```bash
docker compose up -d
```

Start the application:

```bash
./mvnw spring-boot:run
```

If you prefer Maven installed locally:

```bash
mvn spring-boot:run
```

The app uses the database settings from `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://127.0.0.1:5432/vectordb
    username: admin
    password: admin
```

## What is implemented

### 1. Document indexing

When a document is created, the application persists the raw content first and then publishes an event. The event listener is only an inbound adapter now. The indexing use case lives in `KnowledgeDocumentIndexer`:

```java
List<String> chunks = documentChunker.chunk(document.getContent());

for (String chunkText : chunks) {
    EmbeddingVector embedding = embeddingPort.embed(chunkText);

    KnowledgeDocumentChunk chunk =
            KnowledgeDocumentChunk.builder()
                    .documentId(document.getId())
                    .chunkIndex(index++)
                    .chunkText(chunkText)
                    .embedding(embedding.values())
                    .embeddingModel(embedding.modelName())
                    .build();
}
```

That keeps the event adapter thin and moves business logic into the application service.

The configured splitter preserves the original project behavior of one chunk per paragraph, while still using LangChain4j segment types and splitters internally for long paragraphs.

### 2. Hybrid retrieval

Retrieval is not pure vector search. The query supports:

- semantic similarity on `pgvector`
- keyword search using PostgreSQL full-text search
- exact-match metadata filtering with JSONB containment

The repository query combines those signals in a single SQL statement:

```sql
WHERE (:keywordQuery IS NULL
    OR to_tsvector('english', c.chunk_text) @@ websearch_to_tsquery('english', :keywordQuery))
  AND (:metadataFilter IS NULL
    OR d.metadata @> CAST(:metadataFilter AS jsonb))
ORDER BY
    CASE
        WHEN :keywordQuery IS NULL THEN 0
        ELSE ts_rank_cd(
            to_tsvector('english', c.chunk_text),
            websearch_to_tsquery('english', :keywordQuery)
        )
    END DESC,
    c.embedding <-> CAST(:embedding AS vector),
    c.id
```

This gives you a clean baseline for hybrid retrieval without introducing another search engine.

### 3. Prompt construction

After retrieval, the project builds a prompt from the top chunks using LangChain4j `PromptTemplate`, but the application service still works with a plain `String`:

```java
private static final PromptTemplate PROMPT_TEMPLATE = PromptTemplate.from("""
        You are an assistant for a knowledge base.
        Answer only using the context below.
        If the answer is not present in the context, say you do not know.

        Context:
        {{context}}

        User question:
        {{question}}

        Answer:
        """);
```

That logic lives in `PromptBuilder`.

### 4. LangChain4j-backed fake models

The project currently includes:

- `FakeEmbeddingModel`, which implements LangChain4j `EmbeddingModel`
- `FakeChatModel`, which implements LangChain4j `ChatModel`

These keep local development simple while preserving the same adapter flow you would use with a real provider integration.

## API documentation

Swagger UI is available after startup:

- [Swagger UI](http://127.0.0.1:8080/swagger-ui.html)
- [OpenAPI JSON](http://127.0.0.1:8080/v3/api-docs)
- [Actuator](http://127.0.0.1:8080/actuator)
- [Health Check](http://127.0.0.1:8080/actuator/health)

## How to use the API

### 1. Create a knowledge document

`POST /documents`

Request:

```json
{
  "title": "RAG overview",
  "content": "Retrieval-augmented generation combines retrieval with generation. Embeddings help map text into vector space. Metadata can be used to constrain search.",
  "metadata": {
    "source": "docs",
    "team": "platform",
    "language": "en"
  }
}
```

Response:

```json
{
  "id": 1
}
```

What happens next:

- the document is saved with `PENDING` status
- the content is chunked
- embeddings are created for each chunk
- chunks are stored in `knowledge_document_chunk`
- the document finishes as `INDEXED`

### 2. Ask a question with semantic search only

`POST /questions`

Request:

```json
{
  "question": "What is retrieval augmented generation?"
}
```

### 3. Ask a question with hybrid retrieval

`POST /questions`

Request:

```json
{
  "question": "What is RAG?",
  "keywordQuery": "retrieval augmented generation",
  "metadataFilters": {
    "source": "docs",
    "team": "platform"
  }
}
```

Response:

```json
{
  "question": "What is RAG?",
  "answer": "Fake AI response.\nIn a real system, this prompt would be sent through a LangChain4j chat model provider.\n",
  "contextChunks": [
    "Retrieval-augmented generation combines retrieval with generation.",
    "Embeddings help map text into vector space.",
    "Metadata can be used to constrain search."
  ]
}
```

## Data model notes

- `knowledge_document.metadata` is stored as `jsonb`
- `knowledge_document_chunk.embedding` is stored as `vector(1536)`
- the current embedding dimension is fixed to `1536`

That last point matters if you replace the current fake LangChain4j embedding model. If your real embedding model does not produce 1536 dimensions, the column definition and related assumptions must be updated.

## Replacing the fake models

The current integration points are:

- `DocumentChunker` bean in `LangChain4jConfiguration`
- `EmbeddingPort` implemented by `LangChain4jEmbeddingAdapter`
- `ChatPort` implemented by `LangChain4jChatAdapter`
- fake LangChain4j models behind those adapters for local execution

That means the natural next step is to swap those fake beans for real LangChain4j provider implementations while keeping the retrieval layer unchanged:

- `KnowledgeDocumentCreatedListener` still indexes chunk embeddings
- `RetrievalService` still embeds the user question
- PostgreSQL + `pgvector` still handles nearest-neighbor search
- `SemanticSearchService` still builds the final answer from retrieved context
