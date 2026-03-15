package com.example.knowledgebase.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
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

import com.example.knowledgebase.domain.IndexStatus;
import com.example.knowledgebase.domain.KnowledgeDocument;
import com.example.knowledgebase.domain.KnowledgeDocumentChunk;
import com.example.knowledgebase.repository.KnowledgeDocumentChunkRepository;
import com.example.knowledgebase.repository.KnowledgeDocumentRepository;
import com.example.knowledgebase.service.EmbeddingService;
import com.example.knowledgebase.service.TextChunker;

@ExtendWith(MockitoExtension.class)
class KnowledgeDocumentCreatedListenerTest {

    @Mock
    private KnowledgeDocumentRepository documentRepository;

    @Mock
    private KnowledgeDocumentChunkRepository chunkRepository;

    @Mock
    private TextChunker chunker;

    @Mock
    private EmbeddingService embeddingService;

    @InjectMocks
    private KnowledgeDocumentCreatedListener listener;

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
        when(chunker.chunk("ignored")).thenReturn(List.of("chunk 1", "chunk 2"));
        when(embeddingService.generateEmbedding("chunk 1")).thenReturn(new float[] {1.0f, 2.0f});
        when(embeddingService.generateEmbedding("chunk 2")).thenReturn(new float[] {3.0f, 4.0f});
        when(embeddingService.modelName()).thenReturn("test-model");

        listener.handle(new KnowledgeDocumentCreatedEvent(7L));

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
