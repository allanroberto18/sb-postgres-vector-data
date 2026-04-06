package com.example.knowledgebase.search.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.knowledgebase.document.adapter.out.persistence.KnowledgeDocumentChunkRepository;
import com.example.knowledgebase.document.adapter.out.persistence.SimilarChunkProjection;
import com.example.knowledgebase.search.application.port.out.KnowledgeChunkSearchPort;
import com.example.knowledgebase.search.domain.SimilarChunk;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostgresKnowledgeChunkSearchAdapter implements KnowledgeChunkSearchPort {

    private final KnowledgeDocumentChunkRepository chunkRepository;

    @Override
    public List<SimilarChunk> searchTopK(String embedding, String keywordQuery, String metadataFilter, int limit) {
        return chunkRepository.searchTopK(embedding, keywordQuery, metadataFilter, limit).stream()
                .map(this::toDomain)
                .toList();
    }

    private SimilarChunk toDomain(SimilarChunkProjection projection) {
        return new SimilarChunk(
                projection.getId(),
                projection.getDocumentId(),
                projection.getChunkIndex(),
                projection.getChunkText(),
                projection.getDistance()
        );
    }
}
