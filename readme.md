# Knowledge Base with PostgreSQL + pgvector

This project shows a practical RAG-style indexing and retrieval flow on top of Spring Boot, PostgreSQL, and `pgvector`.

The current implementation supports this pipeline:

1. Create a knowledge document through a REST endpoint
2. Split the document into smaller chunks
3. Generate one embedding per chunk
4. Persist vectors in PostgreSQL using `pgvector`
5. Search with semantic similarity, optional keyword ranking, and exact-match metadata filters
6. Build a prompt from the retrieved chunks and send it to the AI client

At the moment, the project uses a fake embedding generator and a fake AI client so the full workflow can be exercised without wiring a provider yet. The code is already structured so you can replace that part with a real embedding provider such as LangChain4j.

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

When a document is created, the application persists the raw content first and then publishes an event. The listener handles chunking and vector generation:

```java
float[] embedding =
        embeddingService.generateEmbedding(chunkText);

KnowledgeDocumentChunk chunk =
        KnowledgeDocumentChunk.builder()
                .documentId(document.getId())
                .chunkIndex(index++)
                .chunkText(chunkText)
                .embedding(embedding)
                .embeddingModel(embeddingService.modelName())
                .build();
```

That flow is implemented in `KnowledgeDocumentCreatedListener`.

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

After retrieval, the project builds a prompt from the top chunks:

```java
prompt.append("""
        You are an assistant for a knowledge base.
        Answer only using the context below.
        If the answer is not present in the context, say you do not know.
        """);
```

That logic lives in `PromptBuilder`.

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
  "answer": "Fake AI response.\nIn a real system, this prompt would be sent to an LLM provider.\n",
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

That last point matters if you replace the fake embedder. If your real embedding model does not produce 1536 dimensions, the column definition and related assumptions must be updated.