package com.example.knowledgebase.document.application.command;

import java.util.Map;

public record CreateKnowledgeDocumentCommand(
        String title,
        String content,
        Map<String, String> metadata
) {

    public CreateKnowledgeDocumentCommand {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
