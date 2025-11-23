package org.rag4j.indexing;

import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.store.ContentStore;

import java.util.List;

/**
 * Service that reads all records using a {@link ContentReader} and then indexes them to a {@link ContentStore}.
 */
public class IndexingService {
    private final ContentStore contentStore;

    public IndexingService(ContentStore contentStore) {
        this.contentStore = contentStore;
    }

    public void indexDocuments(ContentReader contentReader, Splitter splitter) {
        contentReader.read().forEach(document -> indexDocument(document, splitter));
    }

    public void indexDocument(InputDocument document, Splitter splitter) {
        List<Chunk> list = splitter.split(document).stream().toList();
        contentStore.store(list);
    }
}
