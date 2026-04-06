package com.example.knowledgebase.api;

import com.example.knowledgebase.service.SemanticSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuestionSearchController.class)
class QuestionSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SemanticSearchService semanticSearchService;

    @Test
    void returnsAnswerFromSemanticSearchService() throws Exception {

        AskQuestionResponse response = new AskQuestionResponse(
                "What is RAG?",
                "It combines retrieval with generation.",
                List.of("chunk 1", "chunk 2")
        );

        AskQuestionRequest request = new AskQuestionRequest(
                "What is RAG?",
                "retrieval generation",
                Map.of("source", "docs")
        );

        when(semanticSearchService.ask(request)).thenReturn(response);

        mockMvc.perform(post("/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "question": "What is RAG?",
                                  "keywordQuery": "retrieval generation",
                                  "metadataFilters": {
                                    "source": "docs"
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.question").value("What is RAG?"))
                .andExpect(jsonPath("$.answer").value("It combines retrieval with generation."))
                .andExpect(jsonPath("$.contextChunks[0]").value("chunk 1"));

        verify(semanticSearchService).ask(request);
    }

    @Test
    void rejectsBlankQuestion() throws Exception {

        mockMvc.perform(post("/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "question": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
