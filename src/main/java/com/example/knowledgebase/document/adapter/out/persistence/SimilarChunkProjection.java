package com.example.knowledgebase.document.adapter.out.persistence;

public interface SimilarChunkProjection {
    Long getId();
    Long getDocumentId();
    Integer getChunkIndex();
    String getChunkText();
    Double getDistance();
}
