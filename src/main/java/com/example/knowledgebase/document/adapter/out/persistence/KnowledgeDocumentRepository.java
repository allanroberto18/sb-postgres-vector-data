package com.example.knowledgebase.document.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.knowledgebase.document.application.port.out.KnowledgeDocumentStore;
import com.example.knowledgebase.document.domain.KnowledgeDocument;

@Repository
public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocument, Long>, KnowledgeDocumentStore {

}
