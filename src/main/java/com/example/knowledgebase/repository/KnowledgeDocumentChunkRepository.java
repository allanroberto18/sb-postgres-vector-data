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
          id,
          document_id AS documentId,
          chunk_index AS chunkIndex,
          chunk_text AS chunkText,
          embedding <-> CAST(:embedding AS vector) AS distance
      FROM knowledge_document_chunk
      ORDER BY embedding <-> CAST(:embedding AS vector)
      LIMIT :limit
      """, nativeQuery = true)
  List<SimilarChunkProjection> searchTopK(
      @Param("embedding") String embedding,
      @Param("limit") int limit
  );

  void deleteByDocumentId(Long documentId);
}
