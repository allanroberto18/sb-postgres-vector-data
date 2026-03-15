package com.example.knowledgebase.api;

import jakarta.validation.constraints.NotBlank;

public record AskQuestionRequest(@NotBlank String question) {
}
