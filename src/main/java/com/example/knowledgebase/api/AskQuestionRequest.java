package com.example.knowledgebase.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record AskQuestionRequest(
        @Schema(example = "What is retrieval augmented generation?", description = "Natural-language question used for retrieval")
        @NotBlank String question,
        @Schema(example = "retrieval augmented generation", description = "Optional keyword query for PostgreSQL full-text search")
        String keywordQuery,
        @Schema(
                example = "{\"source\":\"docs\",\"team\":\"platform\"}",
                description = "Optional exact-match metadata filters applied against the document JSONB metadata"
        )
        Map<@NotBlank String, @NotBlank String> metadataFilters
) {

    public AskQuestionRequest {
        metadataFilters = metadataFilters == null ? Map.of() : Map.copyOf(metadataFilters);
    }
}
