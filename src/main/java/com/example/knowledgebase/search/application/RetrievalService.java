package com.example.knowledgebase.search.application;

import com.example.knowledgebase.search.application.port.out.KnowledgeChunkSearchPort;
import com.example.knowledgebase.shared.ai.EmbeddingPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RetrievalService {

    private final EmbeddingPort embeddingPort;
    private final KnowledgeChunkSearchPort knowledgeChunkSearchPort;
    private final VectorFormatter vectorFormatter;
    private final ObjectMapper objectMapper;

    public List<String> retrieveRelevantChunks(
            String question,
            String keywordQuery,
            Map<String, String> metadataFilters,
            int topK
    ) {

        float[] questionEmbedding = embeddingPort.embed(question).values();

        String vector = vectorFormatter.toPgVector(questionEmbedding);
        String metadataFilterJson = toMetadataFilterJson(metadataFilters);

        List<com.example.knowledgebase.search.domain.SimilarChunk> results =
                knowledgeChunkSearchPort.searchTopK(vector, normalizeKeywordQuery(keywordQuery), metadataFilterJson, topK);

        return results.stream()
                .map(com.example.knowledgebase.search.domain.SimilarChunk::chunkText)
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
