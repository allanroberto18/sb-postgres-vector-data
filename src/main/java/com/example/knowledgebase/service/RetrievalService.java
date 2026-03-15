package com.example.knowledgebase.service;

import com.example.knowledgebase.repository.KnowledgeDocumentChunkRepository;
import com.example.knowledgebase.repository.SimilarChunkProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetrievalService {

    private final EmbeddingService embeddingService;
    private final KnowledgeDocumentChunkRepository chunkRepository;
    private final VectorFormatter vectorFormatter;

    public List<String> retrieveRelevantChunks(String question, int topK) {

        float[] questionEmbedding = embeddingService.generateEmbedding(question);

        String vector = vectorFormatter.toPgVector(questionEmbedding);

        List<SimilarChunkProjection> results =
                chunkRepository.searchTopK(vector, topK);

        return results.stream()
                .map(SimilarChunkProjection::getChunkText)
                .toList();
    }
}
