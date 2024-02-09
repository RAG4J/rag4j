package org.rag4j.integrations.weaviate.schema;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Text2VecOpenAIPropertyModuleConfig {
    private Boolean skip;
    private Boolean vectorizePropertyName;
}
