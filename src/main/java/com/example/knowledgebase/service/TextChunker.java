package com.example.knowledgebase.service;

import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

@Component
public class TextChunker {

    private static final int MAX_CHARS = 500;

    public List<String> chunk(String text) {

        if (text == null || text.isBlank()) {
            return List.of();
        }

        String[] paragraphs = text.split("\\R\\s*\\R");

        List<String> chunks = new ArrayList<>();

        for (String paragraph : paragraphs) {
            appendParagraph(chunks, paragraph.strip());
        }

        return chunks;
    }

    private void appendParagraph(List<String> chunks, String paragraph) {

        if (paragraph.isEmpty()) {
            return;
        }

        if (paragraph.length() > MAX_CHARS) {
            splitLongParagraph(chunks, paragraph);
            return;
        }

        chunks.add(paragraph);
    }

    private void splitLongParagraph(List<String> chunks, String paragraph) {

        int start = 0;
        while (start < paragraph.length()) {
            int end = Math.min(start + MAX_CHARS, paragraph.length());
            chunks.add(paragraph.substring(start, end));
            start = end;
        }
    }
}
