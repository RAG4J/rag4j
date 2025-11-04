package org.rag4j.rag.tracker;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RAGObserver {
    /**
     * Map of documentChunkId to text of relevant chunks.
     */
    private Map<String,String> relevantChunks = new HashMap<>();

    /**
     * Map of window to chunkIds. Window is the documentChunkId of the relevant chunk in the window.
     */
    private Map<String, List<String>> windowToChunkIds = new HashMap<>();

    /**
     * Map of window to text. Window is the documentChunkId of the relevant chunk in the window.
     */
    private Map<String, String> windowTexts = new HashMap<>();

    /**
     * The question that was asked.
     */
    private String question;

    /**
     * The context that was provided, usually by the retriever.
     */
    private String context;

    /**
     * The generated answer for the question using the provided context.
     */
    private String answer;

    /**
     * Add a window to the chunkIds.
     * @param window The documentChunkId of the relevant chunk in the window.
     * @param chunkIds The chunkIds around the relevant chunks in the window.
     */
    public void addWindowToChunkIds(String window, List<String> chunkIds) {
        windowToChunkIds.put(window, chunkIds);
    }

    /**
     * Add a window to the text.
     * @param window The documentChunkId of the relevant chunk in the window.
     * @param text The text of the window for all the chunks in the window.
     */
    public void addWindowText(String window, String text) {
        windowTexts.put(window, text);
    }

    /**
     * Add a relevant chunk.
     * @param documentChunkId The documentChunkId of the relevant chunk.
     * @param text The text of the relevant chunk.
     */
    public void addRelevantChunk(String documentChunkId, String text) {
        relevantChunks.put(documentChunkId, text);
    }
}
