package com.example.knowledgebase.shared.adapter.out.ai.langchain4j;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.stereotype.Component;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;

@Component
public class FakeEmbeddingModel implements EmbeddingModel {

    static final int DIMENSIONS = 1536;

    @Override
    public Response<List<Embedding>> embedAll(List<TextSegment> textSegments) {
        List<Embedding> embeddings = textSegments.stream()
                .map(TextSegment::text)
                .map(this::createEmbedding)
                .toList();

        return Response.from(embeddings);
    }

    @Override
    public String modelName() {
        return "fake-langchain4j-embedding-model";
    }

    private Embedding createEmbedding(String text) {
        byte[] seed = digest(text);
        ByteBuffer seedBuffer = ByteBuffer.wrap(seed);
        float[] vector = new float[DIMENSIONS];

        for (int i = 0; i < vector.length; i++) {
            int next = seedBuffer.getInt((i * Integer.BYTES) % seed.length);
            vector[i] = (next & 0x7fffffff) / (float) Integer.MAX_VALUE;
        }

        Embedding embedding = Embedding.from(vector);
        embedding.normalize();
        return embedding;
    }

    private byte[] digest(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(text.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
