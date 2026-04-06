package com.example.knowledgebase.document.application;

import org.springframework.stereotype.Service;

import com.example.knowledgebase.document.application.command.CreateKnowledgeDocumentCommand;
import com.example.knowledgebase.document.application.event.KnowledgeDocumentCreated;
import com.example.knowledgebase.document.application.port.out.KnowledgeDocumentCreatedEventPort;
import com.example.knowledgebase.document.application.port.out.KnowledgeDocumentStore;
import com.example.knowledgebase.document.domain.IndexStatus;
import com.example.knowledgebase.document.domain.KnowledgeDocument;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KnowledgeDocumentService {

    private final KnowledgeDocumentStore documentStore;
    private final KnowledgeDocumentCreatedEventPort eventPort;

    @Transactional
    public Long create(CreateKnowledgeDocumentCommand command) {

        KnowledgeDocument document = KnowledgeDocument.builder()
                .title(command.title())
                .content(command.content())
                .metadata(command.metadata())
                .indexStatus(IndexStatus.PENDING)
                .build();

        KnowledgeDocument saved = documentStore.save(document);

        eventPort.publish(new KnowledgeDocumentCreated(saved.getId()));

        return saved.getId();
    }
}
