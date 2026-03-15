package com.example.knowledgebase.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FakeAiClientTest {

    private final FakeAiClient client = new FakeAiClient();

    @Test
    void returnsStaticPlaceholderResponse() {

        String response = client.ask("prompt");

        assertTrue(response.contains("Fake AI response."));
    }
}
