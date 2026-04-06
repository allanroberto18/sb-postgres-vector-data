package com.example.knowledgebase.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record CreateKnowledgeDocumentRequest(
    @Schema(example = "RAG overview", description = "Human-readable title for the source document")
    @NotBlank String title,
    @Schema(
            example = "Retrieval-augmented generation combines retrieval with generation. Embeddings help map text into vector space.",
            description = "Raw document content that will be chunked and indexed"
    )
    @NotBlank String content,
    @Schema(
            example = "{\"source\":\"docs\",\"team\":\"platform\",\"language\":\"en\"}",
            description = "Optional metadata persisted as JSONB and later used for exact-match filtering"
    )
    Map<@NotBlank String, @NotBlank String> metadata
) {

    public CreateKnowledgeDocumentRequest {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
