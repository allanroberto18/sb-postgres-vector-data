package com.example.knowledgebase.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.example.knowledgebase.api.CreateKnowledgeDocumentRequest;
import com.example.knowledgebase.domain.IndexStatus;
import com.example.knowledgebase.domain.KnowledgeDocument;
import com.example.knowledgebase.event.KnowledgeDocumentCreatedEvent;
import com.example.knowledgebase.repository.KnowledgeDocumentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KnowledgeDocumentService {

    private final KnowledgeDocumentRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long create(CreateKnowledgeDocumentRequest request) {

        KnowledgeDocument document = KnowledgeDocument.builder()
                .title(request.title())
                .content(request.content())
                .indexStatus(IndexStatus.PENDING)
                .build();

        KnowledgeDocument saved = repository.save(document);

        eventPublisher.publishEvent(
                new KnowledgeDocumentCreatedEvent(saved.getId())
        );

        return saved.getId();
    }
}
