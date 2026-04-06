package com.example.knowledgebase.document.application.port.out;

import com.example.knowledgebase.document.application.event.KnowledgeDocumentCreated;

public interface KnowledgeDocumentCreatedEventPort {

    void publish(KnowledgeDocumentCreated event);
}
