package com.example.knowledgebase.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(name = "knowledge_document")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> metadata = Map.of();

    @Enumerated(EnumType.STRING)
    private IndexStatus indexStatus;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();

        if (indexStatus == null) {
            indexStatus = IndexStatus.PENDING;
        }

        if (metadata == null) {
            metadata = Map.of();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();

        if (metadata == null) {
            metadata = Map.of();
        }
    }
}
