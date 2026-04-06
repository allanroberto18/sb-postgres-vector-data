package com.example.knowledgebase.shared.adapter.out.ai.langchain4j;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FakeEmbeddingModelTest {

    private final FakeEmbeddingModel model = new FakeEmbeddingModel();

    @Test
    void returnsExpectedModelNameAndDimensions() {
        float[] embedding = model.embed("semantic search").content().vector();
        float[] secondEmbedding = model.embed("semantic search").content().vector();

        assertEquals("fake-langchain4j-embedding-model", model.modelName());
        assertEquals(1536, embedding.length);
        assertArrayEquals(embedding, secondEmbedding);
    }
}
