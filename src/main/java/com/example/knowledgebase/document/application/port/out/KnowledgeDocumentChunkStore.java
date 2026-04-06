package com.example.knowledgebase.document.application.port.out;

import com.example.knowledgebase.document.domain.KnowledgeDocumentChunk;

public interface KnowledgeDocumentChunkStore {

    KnowledgeDocumentChunk save(KnowledgeDocumentChunk chunk);
}
