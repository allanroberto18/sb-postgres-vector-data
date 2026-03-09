# Project Goal

Our goal is to support this workflow:

1. Save a knowledge document through a REST API
2. Automatically split the document into smaller chunks
3. Generate embeddings for each chunk
4. Store those embeddings in PostgreSQL using pgvector

Once indexed, the knowledge base will be ready for semantic search.

### Requirements

- Java 21
- Spring Boot
- Docker
- Postgres