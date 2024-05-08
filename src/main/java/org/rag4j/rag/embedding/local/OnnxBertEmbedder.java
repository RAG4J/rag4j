package org.rag4j.rag.embedding.local;

import org.rag4j.rag.embedding.Embedder;

import java.util.ArrayList;
import java.util.List;

public class OnnxBertEmbedder implements Embedder {
    private final OnnxBertBiEncoder model;

    public OnnxBertEmbedder() {
        this.model = new OnnxBertBiEncoder();
    }

    @Override
    public List<Float> embed(String text) {
        float[] embed = model.embed(text);

        List<Float> list = new ArrayList<>();
        for (float value : embed) {
            list.add(value);
        }
        return list;
    }
}
