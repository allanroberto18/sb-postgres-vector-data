package com.example.knowledgebase.document.adapter.in.event;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.knowledgebase.document.adapter.out.event.KnowledgeDocumentCreatedEvent;
import com.example.knowledgebase.document.application.KnowledgeDocumentIndexer;

@ExtendWith(MockitoExtension.class)
class KnowledgeDocumentCreatedListenerTest {

    @Mock
    private KnowledgeDocumentIndexer indexer;

    @InjectMocks
    private KnowledgeDocumentCreatedListener listener;

    @Test
    void delegatesIndexingToApplicationService() {
        listener.handle(new KnowledgeDocumentCreatedEvent(7L));

        verify(indexer).index(7L);
    }
}
