package com.example.knowledgebase.shared.adapter.out.ai.langchain4j;

import org.springframework.stereotype.Component;

import com.example.knowledgebase.shared.ai.ChatPort;

import dev.langchain4j.model.chat.ChatModel;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LangChain4jChatAdapter implements ChatPort {

    private final ChatModel chatModel;

    @Override
    public String ask(String prompt) {
        return chatModel.chat(prompt);
    }
}
