package org.rag4j.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Value object representing the output of a retrieval strategy. It contains a list of {@link RetrievalOutputItem}s.
 */
@Builder
@Getter
public class RetrievalOutput {
    private List<RetrievalOutputItem> items;

    @Builder
    @Getter
    public static class RetrievalOutputItem {
        private String documentId;
        private int chunkId;
        private String text;
    }

    public String constructContext() {
        StringBuilder sb = new StringBuilder();
        for (RetrievalOutputItem item : items) {
            sb.append(item.getText());
            sb.append(" ");
        }
        return sb.toString();
    }
}
