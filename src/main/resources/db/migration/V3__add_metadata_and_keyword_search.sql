ALTER TABLE knowledge_document
    ADD COLUMN metadata JSONB NOT NULL DEFAULT '{}'::jsonb;

CREATE INDEX idx_knowledge_document_metadata
    ON knowledge_document USING GIN (metadata jsonb_path_ops);

CREATE INDEX idx_knowledge_document_chunk_text_search
    ON knowledge_document_chunk USING GIN (to_tsvector('english', chunk_text));
