package org.rag4j.weaviate.indexer;

import io.weaviate.client.v1.misc.model.BM25Config;
import io.weaviate.client.v1.misc.model.InvertedIndexConfig;
import io.weaviate.client.v1.schema.model.Property;
import io.weaviate.client.v1.schema.model.WeaviateClass;
import org.rag4j.weaviate.schema.Text2VecOpenAIModuleConfig;
import org.rag4j.weaviate.schema.Text2VecOpenAIPropertyModuleConfig;

import java.util.List;
import java.util.Map;

public class WeaviateChunkClassBuilder {


    /**
     * Build a WeaviateClass object for the Chunk class.
     *
     * <a href="https://weaviate.io/developers/weaviate/config-refs/schema">...</a>
     * <a href="https://weaviate.io/developers/weaviate/config-refs/datatypes">...</a>
     * <a href="https://weaviate.io/developers/weaviate/modules/retriever-vectorizer-modules/text2vec-openai">...</a>
     *
     * @return a WeaviateClass object for the Chunk class
     */
    public static WeaviateClass build() {
        List<Property> properties = List.of(
                Property.builder()
                        .name("documentId")
                        .dataType(List.of("text"))
                        .description("The id of the document this chunk belongs to")
                        .moduleConfig(Map.of(
                                "text2vec-openai", Text2VecOpenAIPropertyModuleConfig.builder()
                                        .skip(Boolean.TRUE)
                                        .vectorizePropertyName(Boolean.FALSE)
                                        .build()
                        ))
                        .build(),
                Property.builder()
                        .name("chunkId")
                        .dataType(List.of("int"))
                        .description("The id of this chunk")
                        .moduleConfig(Map.of(
                                "text2vec-openai", Text2VecOpenAIPropertyModuleConfig.builder()
                                        .skip(Boolean.TRUE)
                                        .vectorizePropertyName(Boolean.FALSE)
                                        .build()
                        ))
                        .build(),
                Property.builder()
                        .name("totalChunks")
                        .dataType(List.of("int"))
                        .description("The total number of chunks in the document")
                        .moduleConfig(Map.of(
                                "text2vec-openai", Text2VecOpenAIPropertyModuleConfig.builder()
                                        .skip(Boolean.TRUE)
                                        .vectorizePropertyName(Boolean.FALSE)
                                        .build()
                        ))
                        .build(),
                Property.builder()
                        .name("text")
                        .dataType(List.of("text"))
                        .description("The text of this chunk")
                        .moduleConfig(Map.of(
                                "text2vec-openai", Text2VecOpenAIPropertyModuleConfig.builder()
                                        .skip(Boolean.FALSE)
                                        .vectorizePropertyName(Boolean.FALSE)
                                        .build()
                        ))
                        .build()
        );
        return WeaviateClass.builder()
                .className("Chunk")
                .description("A chunk of text to be indexed by Weaviate")
                .vectorizer("text2vec-openai")
                .moduleConfig(Map.of(
                        "text2vec-openai", Text2VecOpenAIModuleConfig.builder()
                                .model("ada")
                                .modelVersion("002")
                                .type("text")
                                .build()
                ))
                .invertedIndexConfig(InvertedIndexConfig.builder()
                        .bm25(BM25Config.builder()
                                .b(0.75f)
                                .k1(1.2f)
                                .build())
                        .build())
                .vectorIndexType("hnsw")
                .properties(properties)
                .build();

    }
}
