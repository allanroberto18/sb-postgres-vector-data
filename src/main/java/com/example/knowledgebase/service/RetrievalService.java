package com.example.knowledgebase.service;

import com.example.knowledgebase.repository.KnowledgeDocumentChunkRepository;
import com.example.knowledgebase.repository.SimilarChunkProjection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RetrievalService {

    private final EmbeddingService embeddingService;
    private final KnowledgeDocumentChunkRepository chunkRepository;
    private final VectorFormatter vectorFormatter;
    private final ObjectMapper objectMapper;

    public List<String> retrieveRelevantChunks(
            String question,
            String keywordQuery,
            Map<String, String> metadataFilters,
            int topK
    ) {

        float[] questionEmbedding = embeddingService.generateEmbedding(question);

        String vector = vectorFormatter.toPgVector(questionEmbedding);
        String metadataFilterJson = toMetadataFilterJson(metadataFilters);

        List<SimilarChunkProjection> results =
                chunkRepository.searchTopK(vector, normalizeKeywordQuery(keywordQuery), metadataFilterJson, topK);

        return results.stream()
                .map(SimilarChunkProjection::getChunkText)
                .toList();
    }

    private String normalizeKeywordQuery(String keywordQuery) {
        if (keywordQuery == null || keywordQuery.isBlank()) {
            return null;
        }

        return keywordQuery;
    }

    private String toMetadataFilterJson(Map<String, String> metadataFilters) {
        if (metadataFilters == null || metadataFilters.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(metadataFilters);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Unable to serialize metadata filters", exception);
        }
    }
}
