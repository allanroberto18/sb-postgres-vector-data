package com.example.knowledgebase.search.application.port.out;

import java.util.List;

import com.example.knowledgebase.search.domain.SimilarChunk;

public interface KnowledgeChunkSearchPort {

    List<SimilarChunk> searchTopK(String embedding, String keywordQuery, String metadataFilter, int limit);
}
