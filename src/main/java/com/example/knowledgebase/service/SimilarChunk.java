package com.example.knowledgebase.service;

public record SimilarChunk(
    Long id,
    Long documentId,
    Integer chunkIndex,
    String chunkText,
    Double distance
) {
}
