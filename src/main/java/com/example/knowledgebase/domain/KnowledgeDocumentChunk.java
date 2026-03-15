package com.example.knowledgebase.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "knowledge_document_chunk")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeDocumentChunk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long documentId;

    private Integer chunkIndex;

    @Column(columnDefinition = "TEXT")
    private String chunkText;

    @Column(columnDefinition = "vector(1536)")
    private float[] embedding;

    private String embeddingModel;
}
