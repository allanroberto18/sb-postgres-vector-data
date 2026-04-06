package com.example.knowledgebase.shared.adapter.out.ai.langchain4j;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FakeChatModelTest {

    private final FakeChatModel chatModel = new FakeChatModel();

    @Test
    void returnsStaticPlaceholderResponse() {
        String response = chatModel.chat("prompt");

        assertTrue(response.contains("Fake AI response."));
        assertTrue(response.contains("LangChain4j chat model provider."));
    }
}
