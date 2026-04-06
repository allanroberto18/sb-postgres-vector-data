package com.example.knowledgebase.api;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.knowledgebase.service.KnowledgeDocumentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class KnowledgeDocumentController {

    private final KnowledgeDocumentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a knowledge document",
            description = "Persists a document, then triggers chunking and embedding generation for indexing."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Document accepted for indexing",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "id": 1
                            }
                            """)
            )
    )
    public CreateKnowledgeDocumentResponse create(
            @Valid @RequestBody CreateKnowledgeDocumentRequest request
    ) {

        Long id = service.create(request);

        return new CreateKnowledgeDocumentResponse(id);
    }
}
