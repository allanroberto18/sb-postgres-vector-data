package com.example.knowledgebase.document.application.port.out;

import java.util.List;

public interface DocumentChunker {

    List<String> chunk(String text);
}
