package com.example.knowledgebase.api;

import com.example.knowledgebase.service.SemanticSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionSearchController {

    private final SemanticSearchService semanticSearchService;

    @PostMapping
    public AskQuestionResponse ask(@Valid @RequestBody AskQuestionRequest request) {

        return semanticSearchService.ask(request.question());
    }
}
