package com.example.knowledgebase.search.application;

import org.springframework.stereotype.Component;

import dev.langchain4j.model.input.PromptTemplate;

import java.util.List;
import java.util.Map;

@Component
public class PromptBuilder {
    private static final PromptTemplate PROMPT_TEMPLATE = PromptTemplate.from("""
            You are an assistant for a knowledge base.
            Answer only using the context below.
            If the answer is not present in the context, say you do not know.

            Context:
            {{context}}

            User question:
            {{question}}

            Answer:
            """);

    public String build(String question, List<String> contextChunks) {
        return PROMPT_TEMPLATE.apply(Map.of(
                "context", formatContext(contextChunks),
                "question", question
        )).text();
    }

    private String formatContext(List<String> contextChunks) {
        StringBuilder context = new StringBuilder();

        for (int i = 0; i < contextChunks.size(); i++) {
            context.append("\n[")
                    .append(i + 1)
                    .append("] ")
                    .append(contextChunks.get(i));
        }

        return context.toString();
    }
}
