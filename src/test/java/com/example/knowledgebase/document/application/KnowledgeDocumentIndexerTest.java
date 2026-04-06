package com.example.knowledgebase.document.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.knowledgebase.document.application.port.out.DocumentChunker;
import com.example.knowledgebase.document.application.port.out.KnowledgeDocumentChunkStore;
import com.example.knowledgebase.document.application.port.out.KnowledgeDocumentStore;
import com.example.knowledgebase.document.domain.IndexStatus;
import com.example.knowledgebase.document.domain.KnowledgeDocument;
import com.example.knowledgebase.document.domain.KnowledgeDocumentChunk;
import com.example.knowledgebase.shared.ai.EmbeddingPort;
import com.example.knowledgebase.shared.ai.EmbeddingVector;

@ExtendWith(MockitoExtension.class)
class KnowledgeDocumentIndexerTest {

    @Mock
    private KnowledgeDocumentStore documentRepository;

    @Mock
    private KnowledgeDocumentChunkStore chunkRepository;

    @Mock
    private DocumentChunker documentChunker;

    @Mock
    private EmbeddingPort embeddingPort;

    @InjectMocks
    private KnowledgeDocumentIndexer indexer;

    @Captor
    private ArgumentCaptor<KnowledgeDocumentChunk> chunkCaptor;

    @Test
    void indexesChunksAndMarksDocumentAsIndexed() {
        KnowledgeDocument document = KnowledgeDocument.builder()
                .id(7L)
                .content("ignored")
                .indexStatus(IndexStatus.PENDING)
                .build();

        when(documentRepository.findById(7L)).thenReturn(Optional.of(document));
        when(documentChunker.chunk("ignored")).thenReturn(List.of("chunk 1", "chunk 2"));
        when(embeddingPort.embed("chunk 1")).thenReturn(new EmbeddingVector(new float[] {1.0f, 2.0f}, "test-model"));
        when(embeddingPort.embed("chunk 2")).thenReturn(new EmbeddingVector(new float[] {3.0f, 4.0f}, "test-model"));

        indexer.index(7L);

        assertEquals(IndexStatus.INDEXED, document.getIndexStatus());
        verify(chunkRepository, times(2)).save(chunkCaptor.capture());

        List<KnowledgeDocumentChunk> savedChunks = chunkCaptor.getAllValues();
        assertEquals(2, savedChunks.size());
        assertEquals(0, savedChunks.get(0).getChunkIndex());
        assertEquals("chunk 1", savedChunks.get(0).getChunkText());
        assertEquals(7L, savedChunks.get(0).getDocumentId());
        assertEquals("test-model", savedChunks.get(0).getEmbeddingModel());
        assertEquals(1, savedChunks.get(1).getChunkIndex());
        assertEquals("chunk 2", savedChunks.get(1).getChunkText());
    }
}
