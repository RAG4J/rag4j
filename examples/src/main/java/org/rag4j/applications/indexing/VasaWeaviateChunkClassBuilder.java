package org.rag4j.applications.indexing;

import io.weaviate.client.v1.schema.model.Property;
import org.rag4j.integrations.weaviate.indexer.WeaviateChunkClassBuilder;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class VasaWeaviateChunkClassBuilder extends WeaviateChunkClassBuilder {
    @Override
    protected List<Property> additionalProperties() {
        return List.of(
                Property.builder()
                        .name("title")
                        .dataType(List.of("text"))
                        .description("The title of the document this chunk belongs to")
                        .moduleConfig(buildModuleConfig(FALSE))
                        .build(),
                Property.builder()
                        .name("timerange")
                        .dataType(List.of("text"))
                        .description("The timerange of the document this chunk belongs to")
                        .moduleConfig(buildModuleConfig(TRUE))
                        .build()
        );
    }
}
