package com.example.knowledgebase.search.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record AskQuestionResponse(
        @Schema(example = "What is RAG?", description = "The original question submitted by the client")
        String question,
        @Schema(
                example = "Fake AI response. In a real system, this prompt would be sent to an LLM provider.",
                description = "Answer generated from the retrieved context"
        )
        String answer,
        @Schema(description = "Top retrieved chunks used as context for the answer")
        List<String> contextChunks
) {
}
