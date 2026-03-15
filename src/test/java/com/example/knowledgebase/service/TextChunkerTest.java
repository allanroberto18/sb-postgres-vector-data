package com.example.knowledgebase.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class TextChunkerTest {

    private final TextChunker chunker = new TextChunker();

    @Test
    void createsOneChunkPerParagraphSeparatedByBlankLines() {

        String text = """
                Semantic search looks for meaning instead of exact words.

                Embeddings convert text into numeric vectors.

                Vector databases can compare embeddings using similarity search.

                This approach is commonly used in Retrieval-Augmented Generation systems.
                """;

        List<String> chunks = chunker.chunk(text);

        assertEquals(4, chunks.size());
        assertEquals("Semantic search looks for meaning instead of exact words.", chunks.get(0));
        assertEquals("Embeddings convert text into numeric vectors.", chunks.get(1));
        assertEquals("Vector databases can compare embeddings using similarity search.", chunks.get(2));
        assertEquals("This approach is commonly used in Retrieval-Augmented Generation systems.", chunks.get(3));
    }

    @Test
    void supportsWindowsLineEndingsBetweenParagraphs() {

        String text = "First paragraph.\r\n\r\nSecond paragraph.\r\n\r\nThird paragraph.";

        List<String> chunks = chunker.chunk(text);

        assertEquals(List.of("First paragraph.", "Second paragraph.", "Third paragraph."), chunks);
    }

    @Test
    void splitsLongParagraphWithoutCreatingEmptyChunks() {

        String longParagraph = "A".repeat(1200);

        List<String> chunks = chunker.chunk(longParagraph);

        assertEquals(3, chunks.size());
        assertEquals(500, chunks.get(0).length());
        assertEquals(500, chunks.get(1).length());
        assertEquals(200, chunks.get(2).length());
        assertTrue(chunks.stream().noneMatch(String::isEmpty));
    }
}
