package com.example.knowledgebase.service;

import org.springframework.stereotype.Component;

@Component
public class VectorFormatter {
    public String toPgVector(float[] embedding) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {

            builder.append(embedding[i]);

            if (i < embedding.length - 1) {
                builder.append(",");
            }
        }

        builder.append("]");

        return builder.toString();
    }
}
