package org.rag4j.integrations.weaviate.indexer;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import io.weaviate.client.v1.misc.model.BM25Config;
import io.weaviate.client.v1.misc.model.InvertedIndexConfig;
import io.weaviate.client.v1.schema.model.Property;
import io.weaviate.client.v1.schema.model.WeaviateClass;
import org.rag4j.integrations.openai.OpenAIConstants;
import org.rag4j.integrations.weaviate.schema.Text2VecOpenAIModuleConfig;
import org.rag4j.integrations.weaviate.schema.Text2VecOpenAIPropertyModuleConfig;

import static java.lang.Boolean.*;

public class WeaviateChunkClassBuilder {


    /**
     * Build a WeaviateClass object for the Chunk class.
     * <a href="https://weaviate.io/developers/weaviate/config-refs/schema">...</a>
     * <a href="https://weaviate.io/developers/weaviate/config-refs/datatypes">...</a>
     * <a href="https://weaviate.io/developers/weaviate/modules/retriever-vectorizer-modules/text2vec-openai">...</a>
     *
     * @return a WeaviateClass object for the Chunk class
     */
    public WeaviateClass build(String collection) {
        List<Property> properties = defaultProperties();

        List<Property> combined = Stream.concat(properties.stream(), additionalProperties().stream())
                .toList();

        return WeaviateClass.builder()
                .className(collection)
                .description("A chunk of text to be indexed by Weaviate")
                .vectorizer("text2vec-openai")
                .moduleConfig(Map.of(
                        "text2vec-openai", Text2VecOpenAIModuleConfig.builder()
                                .model(OpenAIConstants.DEFAULT_EMBEDDING)
                                .modelVersion(null) // since the new embedding, there is no version
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
                .properties(combined)
                .build();
    }

    private List<Property> defaultProperties() {
        return List.of(
                Property.builder()
                        .name("documentId")
                        .dataType(List.of("text"))
                        .description("The id of the document this chunk belongs to")
                        .moduleConfig(buildModuleConfig(TRUE))
                        .build(),
                Property.builder()
                        .name("chunkId")
                        .dataType(List.of("text"))
                        .description("The id of this chunk")
                        .moduleConfig(buildModuleConfig(TRUE))
                        .build(),
                Property.builder()
                        .name("totalChunks")
                        .dataType(List.of("int"))
                        .description("The total number of chunks in the document")
                        .moduleConfig(buildModuleConfig(TRUE))
                        .build(),
                Property.builder()
                        .name("text")
                        .dataType(List.of("text"))
                        .description("The text of this chunk")
                        .moduleConfig(buildModuleConfig(FALSE))
                        .build()
        );
    }

    protected List<Property> additionalProperties() {
        return List.of();
    }

    protected Map<String, Text2VecOpenAIPropertyModuleConfig> buildModuleConfig(Boolean skip) {

        return Map.of("text2vec-openai", Text2VecOpenAIPropertyModuleConfig.builder()
                .skip(skip)
                .vectorizePropertyName(FALSE)
                .build()
        );
    }
}
