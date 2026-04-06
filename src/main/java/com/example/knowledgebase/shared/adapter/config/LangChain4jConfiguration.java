package com.example.knowledgebase.shared.adapter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.knowledgebase.document.adapter.out.chunking.ParagraphPreservingDocumentSplitter;
import com.example.knowledgebase.document.application.port.out.DocumentChunker;

@Configuration
public class LangChain4jConfiguration {

    private static final int MAX_CHARS = 500;

    @Bean
    public DocumentChunker documentChunker() {
        return new ParagraphPreservingDocumentSplitter(MAX_CHARS);
    }
}
