package com.example.knowledgebase.document.adapter.out.chunking;

import java.util.ArrayList;
import java.util.List;

import com.example.knowledgebase.document.application.port.out.DocumentChunker;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentByCharacterSplitter;
import dev.langchain4j.data.segment.TextSegment;

public class ParagraphPreservingDocumentSplitter implements DocumentChunker {

    private final dev.langchain4j.data.document.DocumentSplitter characterSplitter;

    public ParagraphPreservingDocumentSplitter(int maxSegmentSizeInChars) {
        this.characterSplitter = new DocumentByCharacterSplitter(maxSegmentSizeInChars, 0);
    }

    @Override
    public List<String> chunk(String text) {
        return split(Document.from(text)).stream()
                .map(TextSegment::text)
                .toList();
    }

    List<TextSegment> split(Document document) {
        if (document == null || document.text() == null || document.text().isBlank()) {
            return List.of();
        }

        String[] paragraphs = document.text().split("\\R\\s*\\R");
        List<TextSegment> segments = new ArrayList<>();
        int index = 0;

        for (String paragraph : paragraphs) {
            String normalized = paragraph.strip();

            if (normalized.isEmpty()) {
                continue;
            }

            List<TextSegment> paragraphSegments = characterSplitter.split(Document.from(normalized));

            for (TextSegment paragraphSegment : paragraphSegments) {
                Metadata metadata = document.metadata().copy().put("index", String.valueOf(index++));
                segments.add(TextSegment.from(paragraphSegment.text(), metadata));
            }
        }

        return segments;
    }
}
