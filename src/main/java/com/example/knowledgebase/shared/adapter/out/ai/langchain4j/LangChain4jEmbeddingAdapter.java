package com.example.knowledgebase.shared.adapter.out.ai.langchain4j;

import org.springframework.stereotype.Component;

import com.example.knowledgebase.shared.ai.EmbeddingPort;
import com.example.knowledgebase.shared.ai.EmbeddingVector;

import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LangChain4jEmbeddingAdapter implements EmbeddingPort {

    private final EmbeddingModel embeddingModel;

    @Override
    public EmbeddingVector embed(String text) {
        return new EmbeddingVector(
                embeddingModel.embed(text).content().vector(),
                embeddingModel.modelName()
        );
    }
}
