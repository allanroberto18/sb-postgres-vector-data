package com.example.knowledgebase.document.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.knowledgebase.document.application.command.CreateKnowledgeDocumentCommand;
import com.example.knowledgebase.document.application.event.KnowledgeDocumentCreated;
import com.example.knowledgebase.document.application.port.out.KnowledgeDocumentCreatedEventPort;
import com.example.knowledgebase.document.application.port.out.KnowledgeDocumentStore;
import com.example.knowledgebase.document.domain.IndexStatus;
import com.example.knowledgebase.document.domain.KnowledgeDocument;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class KnowledgeDocumentServiceTest {

    @Mock
    private KnowledgeDocumentStore repository;

    @Mock
    private KnowledgeDocumentCreatedEventPort eventPublisher;

    @InjectMocks
    private KnowledgeDocumentService service;

    @Captor
    private ArgumentCaptor<KnowledgeDocument> documentCaptor;

    @Captor
    private ArgumentCaptor<KnowledgeDocumentCreated> eventCaptor;

    @Test
    void createsPendingDocumentAndPublishesCreatedEvent() {

        CreateKnowledgeDocumentCommand request =
                new CreateKnowledgeDocumentCommand(
                        "Java Virtual Threads",
                        "Some content",
                        Map.of("source", "docs")
                );

        KnowledgeDocument saved = KnowledgeDocument.builder()
                .id(42L)
                .title(request.title())
                .content(request.content())
                .metadata(request.metadata())
                .indexStatus(IndexStatus.PENDING)
                .build();

        when(repository.save(any(KnowledgeDocument.class))).thenReturn(saved);

        Long id = service.create(request);

        assertEquals(42L, id);
        verify(repository).save(documentCaptor.capture());
        verify(eventPublisher).publish(eventCaptor.capture());

        KnowledgeDocument persisted = documentCaptor.getValue();
        assertEquals("Java Virtual Threads", persisted.getTitle());
        assertEquals("Some content", persisted.getContent());
        assertEquals(Map.of("source", "docs"), persisted.getMetadata());
        assertEquals(IndexStatus.PENDING, persisted.getIndexStatus());

        KnowledgeDocumentCreated event = eventCaptor.getValue();
        assertEquals(42L, event.documentId());
    }
}
