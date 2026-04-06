package com.example.knowledgebase.search.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.knowledgebase.search.application.port.out.KnowledgeChunkSearchPort;
import com.example.knowledgebase.search.domain.SimilarChunk;
import com.example.knowledgebase.shared.ai.EmbeddingPort;
import com.example.knowledgebase.shared.ai.EmbeddingVector;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class RetrievalServiceTest {

    @Mock
    private EmbeddingPort embeddingPort;

    @Mock
    private KnowledgeChunkSearchPort chunkRepository;

    @Mock
    private VectorFormatter vectorFormatter;

    private RetrievalService service;

    @Test
    void returnsChunkTextsFromSimilaritySearch() {
        service = new RetrievalService(embeddingPort, chunkRepository, vectorFormatter, new ObjectMapper());

        float[] embedding = new float[] {1.0f, 2.0f};

        when(embeddingPort.embed("What is semantic search?"))
                .thenReturn(new EmbeddingVector(embedding, "test-model"));
        when(vectorFormatter.toPgVector(embedding)).thenReturn("[1.0,2.0]");
        when(chunkRepository.searchTopK("[1.0,2.0]", "bm25 query", "{\"source\":\"docs\"}", 3)).thenReturn(List.of(
                projection("chunk A"),
                projection("chunk B")
        ));

        List<String> chunks = service.retrieveRelevantChunks(
                "What is semantic search?",
                "bm25 query",
                Map.of("source", "docs"),
                3
        );

        assertEquals(List.of("chunk A", "chunk B"), chunks);
        verify(embeddingPort).embed("What is semantic search?");
        verify(vectorFormatter).toPgVector(embedding);
        verify(chunkRepository).searchTopK("[1.0,2.0]", "bm25 query", "{\"source\":\"docs\"}", 3);
    }

    @Test
    void skipsOptionalFiltersWhenBlankOrEmpty() {
        service = new RetrievalService(embeddingPort, chunkRepository, vectorFormatter, new ObjectMapper());

        float[] embedding = new float[] {1.0f, 2.0f};

        when(embeddingPort.embed("What is semantic search?"))
                .thenReturn(new EmbeddingVector(embedding, "test-model"));
        when(vectorFormatter.toPgVector(embedding)).thenReturn("[1.0,2.0]");
        when(chunkRepository.searchTopK("[1.0,2.0]", null, null, 2)).thenReturn(List.of(
                projection("chunk A")
        ));

        List<String> chunks = service.retrieveRelevantChunks(
                "What is semantic search?",
                "   ",
                Map.of(),
                2
        );

        assertEquals(List.of("chunk A"), chunks);
        verify(chunkRepository).searchTopK("[1.0,2.0]", null, null, 2);
    }

    private SimilarChunk projection(String chunkText) {
        return new SimilarChunk(1L, 10L, 0, chunkText, 0.1d);
    }
}
