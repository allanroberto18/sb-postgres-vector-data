package com.example.knowledgebase.api;

import jakarta.validation.constraints.NotBlank;

public record CreateKnowledgeDocumentRequest(
    @NotBlank String title,
    @NotBlank String content
) {

}
