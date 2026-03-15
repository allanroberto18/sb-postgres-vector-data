package com.example.knowledgebase.api;

import com.example.knowledgebase.service.KnowledgeDocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KnowledgeDocumentController.class)
class KnowledgeDocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KnowledgeDocumentService service;

    @Test
    void createsDocumentAndReturnsCreatedStatus() throws Exception {

        when(service.create(new CreateKnowledgeDocumentRequest("Java Virtual Threads", "Chunk me"))).thenReturn(12L);

        mockMvc.perform(post("/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Java Virtual Threads",
                                  "content": "Chunk me"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(12));

        verify(service).create(new CreateKnowledgeDocumentRequest("Java Virtual Threads", "Chunk me"));
    }
}
