package com.example.knowledgebase.service;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class FakeEmbeddingService implements EmbeddingService {

    private static final int DIMENSIONS = 1536;
    private final Random random = new Random();
    
    @Override
    public float[] generateEmbedding(String text) {
        float[] vector = new float[DIMENSIONS];

        for (int i = 0; i < DIMENSIONS; i++) {
            vector[i] = random.nextFloat();
        }

        return vector;
    }
    @Override
    public String modelName() {
        return "fake-embedding-model";
    }
}
