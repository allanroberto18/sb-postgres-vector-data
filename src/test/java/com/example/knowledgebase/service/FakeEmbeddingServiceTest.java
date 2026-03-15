package com.example.knowledgebase.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FakeEmbeddingServiceTest {

    private final FakeEmbeddingService service = new FakeEmbeddingService();

    @Test
    void returnsExpectedModelNameAndDimensions() {

        float[] embedding = service.generateEmbedding("semantic search");

        assertEquals("fake-embedding-model", service.modelName());
        assertEquals(1536, embedding.length);
    }
}
