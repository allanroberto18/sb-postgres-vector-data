package com.example.knowledgebase.api;

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
    public CreateKnowledgeDocumentResponse create(
            @RequestBody CreateKnowledgeDocumentRequest request
    ) {

        Long id = service.create(request);

        return new CreateKnowledgeDocumentResponse(id);
    }
}
