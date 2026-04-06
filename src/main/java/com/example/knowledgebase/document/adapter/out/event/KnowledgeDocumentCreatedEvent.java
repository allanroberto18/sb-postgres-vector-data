package com.example.knowledgebase.document.adapter.out.event;

import com.example.knowledgebase.document.application.event.KnowledgeDocumentCreated;

public record KnowledgeDocumentCreatedEvent(Long documentId) {

    public static KnowledgeDocumentCreatedEvent from(KnowledgeDocumentCreated event) {
        return new KnowledgeDocumentCreatedEvent(event.documentId());
    }
}
