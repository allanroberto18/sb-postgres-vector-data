package com.example.knowledgebase.document.adapter.out.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.knowledgebase.document.application.event.KnowledgeDocumentCreated;
import com.example.knowledgebase.document.application.port.out.KnowledgeDocumentCreatedEventPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpringKnowledgeDocumentCreatedEventPublisher implements KnowledgeDocumentCreatedEventPort {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(KnowledgeDocumentCreated event) {
        applicationEventPublisher.publishEvent(KnowledgeDocumentCreatedEvent.from(event));
    }
}
