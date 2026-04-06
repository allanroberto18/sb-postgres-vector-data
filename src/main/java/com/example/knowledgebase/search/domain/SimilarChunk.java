package com.example.knowledgebase.search.domain;

public record SimilarChunk(
    Long id,
    Long documentId,
    Integer chunkIndex,
    String chunkText,
    Double distance
) {
}
