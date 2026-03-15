package com.example.knowledgebase.service;

import com.example.knowledgebase.api.AskQuestionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SemanticSearchService {

    private static final int TOP_K = 3;

    private final RetrievalService retrievalService;
    private final PromptBuilder promptBuilder;
    private final AiClient aiClient;

    public AskQuestionResponse ask(String question) {

        List<String> contextChunks =
                retrievalService.retrieveRelevantChunks(question, TOP_K);

        String prompt = promptBuilder.build(question, contextChunks);

        String answer = aiClient.ask(prompt);

        return new AskQuestionResponse(
                question,
                answer,
                contextChunks
        );
    }
}
