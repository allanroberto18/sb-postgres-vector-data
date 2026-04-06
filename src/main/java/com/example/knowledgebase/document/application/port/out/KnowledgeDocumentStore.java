package com.example.knowledgebase.document.application.port.out;

import java.util.Optional;

import com.example.knowledgebase.document.domain.KnowledgeDocument;

public interface KnowledgeDocumentStore {

    KnowledgeDocument save(KnowledgeDocument document);

    Optional<KnowledgeDocument> findById(Long id);
}
