package org.rag4j.rag.retrieval;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

/**
 * Value object representing the output of a retrieval strategy. It contains a list of {@link RetrievalOutputItem}s.
 */
@Builder
@Getter
@EqualsAndHashCode
public class RetrievalOutput {
    private List<RetrievalOutputItem> items;

    @Builder
    @Getter
    @EqualsAndHashCode
    public static class RetrievalOutputItem {
        private String documentId;
        private String chunkId;
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
