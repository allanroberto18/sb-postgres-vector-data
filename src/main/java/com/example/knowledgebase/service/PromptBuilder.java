package com.example.knowledgebase.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromptBuilder {
    public String build(String question, List<String> contextChunks) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
                You are an assistant for a knowledge base.
                Answer only using the context below.
                If the answer is not present in the context, say you do not know.

                Context:
                """);

        for (int i = 0; i < contextChunks.size(); i++) {

            prompt.append("\n[")
                    .append(i + 1)
                    .append("] ")
                    .append(contextChunks.get(i));
        }

        prompt.append("\n\nUser question:\n");
        prompt.append(question);

        prompt.append("\n\nAnswer:");

        return prompt.toString();
    }
}
