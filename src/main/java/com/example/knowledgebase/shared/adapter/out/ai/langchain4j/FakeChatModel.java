package com.example.knowledgebase.shared.adapter.out.ai.langchain4j;

import org.springframework.stereotype.Component;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;

@Component
public class FakeChatModel implements ChatModel {

    @Override
    public ChatResponse doChat(ChatRequest chatRequest) {
        return ChatResponse.builder()
                .aiMessage(new AiMessage("""
                        Fake AI response.
                        In a real system, this prompt would be sent through a LangChain4j chat model provider.
                        """))
                .modelName("fake-langchain4j-chat-model")
                .build();
    }
}
