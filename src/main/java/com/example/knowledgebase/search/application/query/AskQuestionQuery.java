package com.example.knowledgebase.search.application.query;

import java.util.Map;

public record AskQuestionQuery(
        String question,
        String keywordQuery,
        Map<String, String> metadataFilters
) {

    public AskQuestionQuery {
        metadataFilters = metadataFilters == null ? Map.of() : Map.copyOf(metadataFilters);
    }
}
