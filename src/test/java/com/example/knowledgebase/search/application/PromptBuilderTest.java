package com.example.knowledgebase.search.application;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class PromptBuilderTest {

    private final PromptBuilder promptBuilder = new PromptBuilder();

    @Test
    void includesContextAndQuestionInPrompt() {

        String prompt = promptBuilder.build(
                "What are embeddings?",
                List.of("Embeddings are vectors.", "Similarity search compares them.")
        );

        assertTrue(prompt.contains("Context:"));
        assertTrue(prompt.contains("[1] Embeddings are vectors."));
        assertTrue(prompt.contains("[2] Similarity search compares them."));
        assertTrue(prompt.contains("User question:\nWhat are embeddings?"));
        assertTrue(prompt.endsWith("\nAnswer:\n"));
    }
}
