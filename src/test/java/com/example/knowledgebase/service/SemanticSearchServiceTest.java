package com.example.knowledgebase.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.knowledgebase.api.AskQuestionRequest;
import com.example.knowledgebase.api.AskQuestionResponse;

@ExtendWith(MockitoExtension.class)
class SemanticSearchServiceTest {

    @Mock
    private RetrievalService retrievalService;

    @Mock
    private PromptBuilder promptBuilder;

    @Mock
    private AiClient aiClient;

    @InjectMocks
    private SemanticSearchService service;

    @Test
    void buildsPromptFromRetrievedContextAndReturnsAnswer() {

        List<String> chunks = List.of("chunk 1", "chunk 2");
        AskQuestionRequest request = new AskQuestionRequest(
                "What is RAG?",
                "retrieval generation",
                Map.of("source", "docs")
        );

        when(retrievalService.retrieveRelevantChunks(
                "What is RAG?",
                "retrieval generation",
                Map.of("source", "docs"),
                3
        )).thenReturn(chunks);
        when(promptBuilder.build("What is RAG?", chunks)).thenReturn("prompt");
        when(aiClient.ask("prompt")).thenReturn("retrieval augmented generation");

        AskQuestionResponse response = service.ask(request);

        assertEquals("What is RAG?", response.question());
        assertEquals("retrieval augmented generation", response.answer());
        assertEquals(chunks, response.contextChunks());
        verify(retrievalService).retrieveRelevantChunks(
                "What is RAG?",
                "retrieval generation",
                Map.of("source", "docs"),
                3
        );
        verify(promptBuilder).build("What is RAG?", chunks);
        verify(aiClient).ask("prompt");
    }
}
