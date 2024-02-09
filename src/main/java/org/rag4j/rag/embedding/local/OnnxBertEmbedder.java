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
    public List<Double> embed(String text) {
        float[] embed = model.embed(text);

        List<Double> doubleList = new ArrayList<>();
        for (float value : embed) {
            doubleList.add((double) value);
        }
        return doubleList;
    }
}
