package com.example.knowledgebase.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class TextChunker {
    
    private static final int MAX_CHARS = 500;

    public List<String> chunk(String text) {

        String[] paragraphs = text.split("\\n\\s*\\n");

        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String paragraph : paragraphs) {

            if (current.length() + paragraph.length() > MAX_CHARS) {
                chunks.add(current.toString());
                current = new StringBuilder();
            }

            current.append(paragraph).append("\n\n");
        }

        if (!current.isEmpty()) {
            chunks.add(current.toString());
        }

        return chunks;
    }
}
