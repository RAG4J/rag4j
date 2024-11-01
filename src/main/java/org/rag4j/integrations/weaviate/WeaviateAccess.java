package org.rag4j.integrations.weaviate;

import java.util.Map;

import io.weaviate.client.Config;
import io.weaviate.client.WeaviateAuthClient;
import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.auth.exception.AuthException;
import io.weaviate.client.v1.data.model.WeaviateObject;
import io.weaviate.client.v1.misc.model.Meta;
import io.weaviate.client.v1.schema.model.WeaviateClass;
import lombok.Getter;
import org.rag4j.util.keyloader.KeyLoader;
import org.slf4j.Logger;

@Getter
public class WeaviateAccess {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(WeaviateAccess.class);
    private final WeaviateClient client;

    public WeaviateAccess(KeyLoader keyLoader) {
        String weaviateURL = keyLoader.getWeaviateURL();
        if (weaviateURL.startsWith("https://")) {
            weaviateURL = weaviateURL.substring(8);
        }
        Config config = new Config("https", weaviateURL);
        try {
            this.client = WeaviateAuthClient.apiKey(config, keyLoader.getWeaviateAPIKey());
            LOGGER.info("Connected to Weaviate host: {}", keyLoader.getWeaviateURL());
        } catch (AuthException e) {
            LOGGER.error("Cannot connect to Weaviate.",e);
            throw new WeaviateException("Cannot connect to Weaviate: " + e.getMessage());
        }
        logClusterMeta();
    }

    public String getSchemaForClass(String className) {
        Result<WeaviateClass> result = client.schema().classGetter().withClassName(className).run();
        if (result.getError() != null) {
            LOGGER.error("Error: {}", result.getError().getMessages());
            return null;
        }
        return result.getResult().toString();
    }

    public void deleteSchema() {
        Result<Boolean> result = client.schema().allDeleter().run();
        if (result.getError() != null) {
            LOGGER.error("Error: {}", result.getError().getMessages());
        }
    }

    public void deleteClass(String className) {
        Result<Boolean> result = client.schema().classDeleter().withClassName(className).run();
        if (result.getError() != null) {
            LOGGER.error("Error: {}", result.getError().getMessages());
        }

        if (result.getResult() != null) {
            LOGGER.info("Deleted class: {} with result {}", className, result.getResult());
        }
    }

    public boolean doesClassExist(String className) {
        Result<Boolean> result = client.schema().exists().withClassName(className).run();
        if (result.getError() != null) {
            LOGGER.error("Error: {}", result.getError().getMessages());
            return false;
        }
        return result.getResult();
    }

    public void createClass(WeaviateClass weaviateClass) {
        Result<Boolean> result = client.schema().classCreator().withClass(weaviateClass).run();
        if (result.getError() != null) {
            LOGGER.error("Error: {}", result.getError().getMessages());
        }

        if (result.getResult() != null) {
            LOGGER.info("Created class: {} with result {}", weaviateClass.getClassName(), result.getResult());
        }

    }

    public void forceCreateClass(WeaviateClass weaviateClass) {
        if (doesClassExist(weaviateClass.getClassName())) {
            LOGGER.info("Class {} already exists. Deleting it.", weaviateClass.getClassName());
            deleteClass(weaviateClass.getClassName());
        }
        createClass(weaviateClass);
    }

    public void addDocument(String className, String documentId, Map<String, Object> properties, Float[] vector) {
        Result<WeaviateObject> result = client.data().creator()
                .withClassName(className)
                .withID(documentId)
                .withProperties(properties)
                .withVector(vector)
                .run();
        if (result.getError() != null) {
            LOGGER.error("Error: {}", result.getError().getMessages());
            throw new WeaviateException(
                    "Error while adding document: " + result.getError().getMessages().getFirst().getMessage());
        }
    }

    public void logClusterMeta() {
        Result<Meta> meta = client.misc().metaGetter().run();
        if (meta.getError() == null) {
            LOGGER.info("meta.hostname: {}", meta.getResult().getHostname());
            LOGGER.info("meta.version: {}", meta.getResult().getVersion());
            LOGGER.info("meta.modules: {}", meta.getResult().getModules());
        } else {
            LOGGER.error("Error: {}", meta.getError().getMessages());
        }
    }

}
