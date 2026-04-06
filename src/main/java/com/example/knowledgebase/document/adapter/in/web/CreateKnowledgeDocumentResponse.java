package com.example.knowledgebase.document.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateKnowledgeDocumentResponse(
        @Schema(example = "1", description = "Generated document identifier")
        Long id
) {

}
