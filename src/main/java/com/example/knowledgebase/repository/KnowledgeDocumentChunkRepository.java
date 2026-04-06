package com.example.knowledgebase.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.knowledgebase.domain.KnowledgeDocumentChunk;

@Repository
public interface KnowledgeDocumentChunkRepository extends JpaRepository<KnowledgeDocumentChunk, Long> {
  List<KnowledgeDocumentChunk> findByDocumentIdOrderByChunkIndexAsc(Long documentId);

  @Query(value = """
      SELECT
          c.id,
          c.document_id AS documentId,
          c.chunk_index AS chunkIndex,
          c.chunk_text AS chunkText,
          c.embedding <-> CAST(:embedding AS vector) AS distance
      FROM knowledge_document_chunk c
      JOIN knowledge_document d ON d.id = c.document_id
      WHERE (:keywordQuery IS NULL
          OR to_tsvector('english', c.chunk_text) @@ websearch_to_tsquery('english', :keywordQuery))
        AND (:metadataFilter IS NULL
          OR d.metadata @> CAST(:metadataFilter AS jsonb))
      ORDER BY
          CASE
              WHEN :keywordQuery IS NULL THEN 0
              ELSE ts_rank_cd(
                  to_tsvector('english', c.chunk_text),
                  websearch_to_tsquery('english', :keywordQuery)
              )
          END DESC,
          c.embedding <-> CAST(:embedding AS vector),
          c.id
      LIMIT :limit
      """, nativeQuery = true)
  List<SimilarChunkProjection> searchTopK(
      @Param("embedding") String embedding,
      @Param("keywordQuery") String keywordQuery,
      @Param("metadataFilter") String metadataFilter,
      @Param("limit") int limit
  );

  void deleteByDocumentId(Long documentId);
}
