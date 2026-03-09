package com.example.knowledgebase.event;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.knowledgebase.domain.IndexStatus;
import com.example.knowledgebase.domain.KnowledgeDocument;
import com.example.knowledgebase.domain.KnowledgeDocumentChunk;
import com.example.knowledgebase.repository.KnowledgeDocumentChunkRepository;
import com.example.knowledgebase.repository.KnowledgeDocumentRepository;
import com.example.knowledgebase.service.EmbeddingService;
import com.example.knowledgebase.service.TextChunker;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KnowledgeDocumentCreatedListener {
    
    private final KnowledgeDocumentRepository documentRepository;
    private final KnowledgeDocumentChunkRepository chunkRepository;
    private final TextChunker chunker;
    private final EmbeddingService embeddingService;

    @Transactional
    @EventListener
    public void handle(KnowledgeDocumentCreatedEvent event) {

        KnowledgeDocument document =
                documentRepository.findById(event.documentId())
                        .orElseThrow();

        document.setIndexStatus(IndexStatus.INDEXING);

        List<String> chunks = chunker.chunk(document.getContent());

        int index = 0;

        for (String chunkText : chunks) {

            float[] embedding =
                    embeddingService.generateEmbedding(chunkText);

            KnowledgeDocumentChunk chunk =
                    KnowledgeDocumentChunk.builder()
                            .documentId(document.getId())
                            .chunkIndex(index++)
                            .chunkText(chunkText)
                            .embedding(embedding)
                            .embeddingModel(embeddingService.modelName())
                            .build();

            chunkRepository.save(chunk);
        }

        document.setIndexStatus(IndexStatus.INDEXED);
    }

}
