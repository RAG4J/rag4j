package org.rag4j.integrations.weaviate.schema;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Text2VecOpenAIModuleConfig {
    private String model;
    private String modelVersion;
    private String type;
    private String baseURL;
    private Boolean vectorizeClassName;
}
