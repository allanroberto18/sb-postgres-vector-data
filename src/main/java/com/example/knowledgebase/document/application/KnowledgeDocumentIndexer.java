package com.example.knowledgebase.document.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.knowledgebase.document.application.port.out.DocumentChunker;
import com.example.knowledgebase.document.application.port.out.KnowledgeDocumentChunkStore;
import com.example.knowledgebase.document.application.port.out.KnowledgeDocumentStore;
import com.example.knowledgebase.document.domain.IndexStatus;
import com.example.knowledgebase.document.domain.KnowledgeDocument;
import com.example.knowledgebase.document.domain.KnowledgeDocumentChunk;
import com.example.knowledgebase.shared.ai.EmbeddingPort;
import com.example.knowledgebase.shared.ai.EmbeddingVector;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KnowledgeDocumentIndexer {

    private final KnowledgeDocumentStore documentStore;
    private final KnowledgeDocumentChunkStore chunkStore;
    private final DocumentChunker documentChunker;
    private final EmbeddingPort embeddingPort;

    @Transactional
    public void index(Long documentId) {
        KnowledgeDocument document = documentStore.findById(documentId).orElseThrow();
        document.setIndexStatus(IndexStatus.INDEXING);

        List<String> chunks = documentChunker.chunk(document.getContent());

        int index = 0;
        for (String chunkText : chunks) {
            EmbeddingVector embedding = embeddingPort.embed(chunkText);

            KnowledgeDocumentChunk knowledgeChunk = KnowledgeDocumentChunk.builder()
                    .documentId(document.getId())
                    .chunkIndex(index++)
                    .chunkText(chunkText)
                    .embedding(embedding.values())
                    .embeddingModel(embedding.modelName())
                    .build();

            chunkStore.save(knowledgeChunk);
        }

        document.setIndexStatus(IndexStatus.INDEXED);
    }
}
