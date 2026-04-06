package com.example.knowledgebase.search.adapter.in.web;

import com.example.knowledgebase.search.application.query.AskQuestionQuery;
import com.example.knowledgebase.search.application.SemanticSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(
            summary = "Ask a question against the indexed knowledge base",
            description = "Runs semantic retrieval with optional keyword ranking and metadata filters, then builds an answer from the retrieved context."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Answer generated from retrieved context",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "question": "What is RAG?",
                              "answer": "Fake AI response. In a real system, this prompt would be sent to an LLM provider.",
                              "contextChunks": [
                                "Retrieval-augmented generation combines retrieval with generation.",
                                "Embeddings help map text into vector space."
                              ]
                            }
                            """)
            )
    )
    public AskQuestionResponse ask(@Valid @RequestBody AskQuestionRequest request) {

        return semanticSearchService.ask(new AskQuestionQuery(
                request.question(),
                request.keywordQuery(),
                request.metadataFilters()
        ));
    }
}
