package com.example.knowledgebase.search.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class VectorFormatterTest {

    private final VectorFormatter formatter = new VectorFormatter();

    @Test
    void formatsEmbeddingAsPgVectorLiteral() {

        String vector = formatter.toPgVector(new float[] {1.5f, 2.25f, 3.0f});

        assertEquals("[1.5,2.25,3.0]", vector);
    }
}
