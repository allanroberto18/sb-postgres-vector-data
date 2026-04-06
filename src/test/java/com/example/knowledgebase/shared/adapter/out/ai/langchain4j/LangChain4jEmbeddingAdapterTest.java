package com.example.knowledgebase.shared.adapter.out.ai.langchain4j;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import com.example.knowledgebase.shared.ai.EmbeddingVector;

@ExtendWith(MockitoExtension.class)
class LangChain4jEmbeddingAdapterTest {

    @Mock
    private EmbeddingModel embeddingModel;

    @InjectMocks
    private LangChain4jEmbeddingAdapter adapter;

    @Test
    void delegatesEmbeddingToLangChain4jModel() {
        float[] vector = new float[] {1.0f, 2.0f};
        when(embeddingModel.embed("text")).thenReturn(Response.from(Embedding.from(vector)));
        when(embeddingModel.modelName()).thenReturn("test-model");

        EmbeddingVector result = adapter.embed("text");

        assertArrayEquals(vector, result.values());
        assertEquals("test-model", result.modelName());
        verify(embeddingModel).embed("text");
    }
}
