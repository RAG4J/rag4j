package org.rag4j.rag.retrieval.quality;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

/**
 * Value object that contains the ids of the correct and incorrect retrieved chunks. The class is used to evaluate the
 * quality of the retrieval. The class provides the precision and the total number of items.
 */
@Getter
@AllArgsConstructor
public class RetrievalQuality {
    private Set<String> correct;
    private Set<String> incorrect;

    public double getPrecision() {
        return (double) correct.size() / (correct.size() + incorrect.size());
    }

    public int totalItems() {
        return correct.size() + incorrect.size();
    }
}
