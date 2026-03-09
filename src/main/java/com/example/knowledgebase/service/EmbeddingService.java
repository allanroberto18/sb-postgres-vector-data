package com.example.knowledgebase.service;

public interface EmbeddingService {
    float[] generateEmbedding(String text);

    String modelName();
}
