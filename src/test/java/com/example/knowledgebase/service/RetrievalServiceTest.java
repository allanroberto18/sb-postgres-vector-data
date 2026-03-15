package com.example.knowledgebase.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.knowledgebase.repository.KnowledgeDocumentChunkRepository;
import com.example.knowledgebase.repository.SimilarChunkProjection;

@ExtendWith(MockitoExtension.class)
class RetrievalServiceTest {

    @Mock
    private EmbeddingService embeddingService;

    @Mock
    private KnowledgeDocumentChunkRepository chunkRepository;

    @Mock
    private VectorFormatter vectorFormatter;

    @InjectMocks
    private RetrievalService service;

    @Test
    void returnsChunkTextsFromSimilaritySearch() {

        float[] embedding = new float[] {1.0f, 2.0f};

        when(embeddingService.generateEmbedding("What is semantic search?"))
                .thenReturn(embedding);
        when(vectorFormatter.toPgVector(embedding)).thenReturn("[1.0,2.0]");
        when(chunkRepository.searchTopK("[1.0,2.0]", 3)).thenReturn(List.of(
                projection("chunk A"),
                projection("chunk B")
        ));

        List<String> chunks = service.retrieveRelevantChunks("What is semantic search?", 3);

        assertEquals(List.of("chunk A", "chunk B"), chunks);
        verify(embeddingService).generateEmbedding("What is semantic search?");
        verify(vectorFormatter).toPgVector(embedding);
        verify(chunkRepository).searchTopK("[1.0,2.0]", 3);
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
