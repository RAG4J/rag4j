package org.rag4j.examples;

import org.rag4j.util.KeyLoader;
import org.rag4j.weaviate.WeaviateAccess;

public class AppWeaviateInfo {
    public static void main(String[] args) {
        // Load the different api keys from the environment
        KeyLoader keyLoader = new KeyLoader();

        // initialize Weaviate
        WeaviateAccess weaviateAccess = new WeaviateAccess(keyLoader);
        weaviateAccess.logClusterMeta();
    }
}
