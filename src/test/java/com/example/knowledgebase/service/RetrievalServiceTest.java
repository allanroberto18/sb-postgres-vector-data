package com.example.knowledgebase.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.knowledgebase.repository.KnowledgeDocumentChunkRepository;
import com.example.knowledgebase.repository.SimilarChunkProjection;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class RetrievalServiceTest {

    @Mock
    private EmbeddingService embeddingService;

    @Mock
    private KnowledgeDocumentChunkRepository chunkRepository;

    @Mock
    private VectorFormatter vectorFormatter;

    private RetrievalService service;

    @Test
    void returnsChunkTextsFromSimilaritySearch() {
        service = new RetrievalService(embeddingService, chunkRepository, vectorFormatter, new ObjectMapper());

        float[] embedding = new float[] {1.0f, 2.0f};

        when(embeddingService.generateEmbedding("What is semantic search?"))
                .thenReturn(embedding);
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
        verify(embeddingService).generateEmbedding("What is semantic search?");
        verify(vectorFormatter).toPgVector(embedding);
        verify(chunkRepository).searchTopK("[1.0,2.0]", "bm25 query", "{\"source\":\"docs\"}", 3);
    }

    @Test
    void skipsOptionalFiltersWhenBlankOrEmpty() {
        service = new RetrievalService(embeddingService, chunkRepository, vectorFormatter, new ObjectMapper());

        float[] embedding = new float[] {1.0f, 2.0f};

        when(embeddingService.generateEmbedding("What is semantic search?"))
                .thenReturn(embedding);
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

    private SimilarChunkProjection projection(String chunkText) {
        return new SimilarChunkProjection() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public Long getDocumentId() {
                return 10L;
            }

            @Override
            public Integer getChunkIndex() {
                return 0;
            }

            @Override
            public String getChunkText() {
                return chunkText;
            }

            @Override
            public Double getDistance() {
                return 0.1d;
            }
        };
    }
}
