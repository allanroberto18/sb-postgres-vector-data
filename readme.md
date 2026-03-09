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

### Start Docker 

```shell
docker compose up -d
``` 

### Endpoints

1. [Actuator](http://127.0.0.1:8080/actuator)
2. [Health Check](http://127.0.0.1:8080/actuator/health)
3. [Open API](http://127.0.0.1:8080/swagger-ui.html)

