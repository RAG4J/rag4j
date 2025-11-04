package org.rag4j.applications.integration;

import org.rag4j.util.keyloader.KeyLoader;
import org.rag4j.integrations.weaviate.WeaviateAccess;

public class AppWeaviateInfo {
    public static void main(String[] args) {
        // Load the different api keys from the environment
        KeyLoader keyLoader = new KeyLoader();

        // initialize Weaviate
        WeaviateAccess weaviateAccess = new WeaviateAccess(keyLoader);
        weaviateAccess.logClusterMeta();
    }
}
