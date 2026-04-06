package com.example.knowledgebase.search.application;

import com.example.knowledgebase.search.adapter.in.web.AskQuestionResponse;
import com.example.knowledgebase.search.application.query.AskQuestionQuery;
import com.example.knowledgebase.shared.ai.ChatPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SemanticSearchService {

    private static final int TOP_K = 3;

    private final RetrievalService retrievalService;
    private final PromptBuilder promptBuilder;
    private final ChatPort chatPort;

    public AskQuestionResponse ask(AskQuestionQuery query) {

        List<String> contextChunks =
                retrievalService.retrieveRelevantChunks(
                        query.question(),
                        query.keywordQuery(),
                        query.metadataFilters(),
                        TOP_K
                );

        String prompt = promptBuilder.build(query.question(), contextChunks);

        String answer = chatPort.ask(prompt);

        return new AskQuestionResponse(
                query.question(),
                answer,
                contextChunks
        );
    }
}
