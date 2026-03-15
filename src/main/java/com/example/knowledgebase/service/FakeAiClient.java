package com.example.knowledgebase.service;

import org.springframework.stereotype.Service;

@Service
public class FakeAiClient implements AiClient {

    @Override
    public String ask(String prompt) {
        return """
                Fake AI response.
                In a real system, this prompt would be sent to an LLM provider.
               """;
    }
}
