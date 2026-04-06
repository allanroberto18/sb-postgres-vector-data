package com.example.knowledgebase.document.adapter.in.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.knowledgebase.document.application.KnowledgeDocumentIndexer;
import com.example.knowledgebase.document.adapter.out.event.KnowledgeDocumentCreatedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KnowledgeDocumentCreatedListener {

    private final KnowledgeDocumentIndexer indexer;

    @Transactional
    @EventListener
    public void handle(KnowledgeDocumentCreatedEvent event) {
        indexer.index(event.documentId());
    }

}
