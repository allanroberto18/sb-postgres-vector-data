package com.example.knowledgebase.repository;

public interface SimilarChunkProjection {
    Long getId();
    Long getDocumentId();
    Integer getChunkIndex();
    String getChunkText();
    Double getDistance();
}
