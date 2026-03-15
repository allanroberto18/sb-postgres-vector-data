package com.example.knowledgebase.service;

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
import org.springframework.context.ApplicationEventPublisher;

import com.example.knowledgebase.api.CreateKnowledgeDocumentRequest;
import com.example.knowledgebase.domain.IndexStatus;
import com.example.knowledgebase.domain.KnowledgeDocument;
import com.example.knowledgebase.event.KnowledgeDocumentCreatedEvent;
import com.example.knowledgebase.repository.KnowledgeDocumentRepository;

@ExtendWith(MockitoExtension.class)
class KnowledgeDocumentServiceTest {

    @Mock
    private KnowledgeDocumentRepository repository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private KnowledgeDocumentService service;

    @Captor
    private ArgumentCaptor<KnowledgeDocument> documentCaptor;

    @Captor
    private ArgumentCaptor<KnowledgeDocumentCreatedEvent> eventCaptor;

    @Test
    void createsPendingDocumentAndPublishesCreatedEvent() {

        CreateKnowledgeDocumentRequest request =
                new CreateKnowledgeDocumentRequest("Java Virtual Threads", "Some content");

        KnowledgeDocument saved = KnowledgeDocument.builder()
                .id(42L)
                .title(request.title())
                .content(request.content())
                .indexStatus(IndexStatus.PENDING)
                .build();

        when(repository.save(any(KnowledgeDocument.class))).thenReturn(saved);

        Long id = service.create(request);

        assertEquals(42L, id);
        verify(repository).save(documentCaptor.capture());
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        KnowledgeDocument persisted = documentCaptor.getValue();
        assertEquals("Java Virtual Threads", persisted.getTitle());
        assertEquals("Some content", persisted.getContent());
        assertEquals(IndexStatus.PENDING, persisted.getIndexStatus());

        KnowledgeDocumentCreatedEvent event = eventCaptor.getValue();
        assertEquals(42L, event.documentId());
    }
}
