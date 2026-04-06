package com.example.knowledgebase.shared.ai;

public interface EmbeddingPort {
    EmbeddingVector embed(String text);
}
