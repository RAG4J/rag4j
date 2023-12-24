package org.rag4j.localembedder;

import org.rag4j.indexing.Embedder;

import java.util.ArrayList;
import java.util.List;

public class AllMiniLmL6V2QEmbedder implements Embedder {
    private final OnnxBertBiEncoder model;

    public AllMiniLmL6V2QEmbedder() {
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
