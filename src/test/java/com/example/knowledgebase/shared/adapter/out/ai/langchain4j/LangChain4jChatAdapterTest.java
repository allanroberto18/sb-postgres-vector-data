package com.example.knowledgebase.shared.adapter.out.ai.langchain4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.langchain4j.model.chat.ChatModel;

@ExtendWith(MockitoExtension.class)
class LangChain4jChatAdapterTest {

    @Mock
    private ChatModel chatModel;

    @InjectMocks
    private LangChain4jChatAdapter adapter;

    @Test
    void delegatesPromptToLangChain4jChatModel() {
        when(chatModel.chat("prompt")).thenReturn("answer");

        assertEquals("answer", adapter.ask("prompt"));
        verify(chatModel).chat("prompt");
    }
}
