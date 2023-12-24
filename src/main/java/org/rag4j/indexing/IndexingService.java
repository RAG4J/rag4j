package org.rag4j.indexing;

/**
 * Service that reads all records using a {@link ContentReader} and then indexes them to a {@link ContentStore}.
 */
public class IndexingService {
    private final ContentStore contentStore;

    public IndexingService(ContentStore contentStore) {
        this.contentStore = contentStore;
    }

    public void indexDocuments(ContentReader contentReader, Splitter splitter) {
        contentReader.read().forEach(document -> contentStore.store(document, splitter));
    }
}
