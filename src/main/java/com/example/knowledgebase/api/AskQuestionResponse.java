package com.example.knowledgebase.api;

import java.util.List;

public record AskQuestionResponse(String question, String answer, List<String> contextChunks) {
}
