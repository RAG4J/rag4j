package org.rag4j.integrations.weaviate.retrieval;

import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import org.rag4j.integrations.weaviate.WeaviateContants;
import org.rag4j.integrations.weaviate.WeaviateException;
import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.model.RelevantChunk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeaviateResponseParser {
    public static List<Chunk> parseGraphQLResponseList(GraphQLResponse response) {
        Map<String,Object> data = parseData(response);
        Map<String,Object> get = parseGet(data);
        List<Map<String, Object>> chunks = parseChunks(get);

        return chunks.stream().map(WeaviateResponseParser::extractChunk).toList();
    }

    public static List<RelevantChunk> parseGraphQLRelevantResponse(GraphQLResponse response) {
        Map<String,Object> data = parseData(response);
        Map<String,Object> get = parseGet(data);
        List<Map<String, Object>> chunks = parseChunks(get);

        return chunks.stream().map(chunk -> {
            Chunk extractedChunk = extractChunk(chunk);

            Map<String,Object> additional = parseAdditional(chunk);
            Double score = (Double) additional.get("distance");
            if (null == score) {
                score = Double.parseDouble((String) additional.get("score"));
            }

            return new RelevantChunk(extractedChunk, score);
        }).toList();
    }

    private static Chunk extractChunk(Map<String, Object> chunk) {
        String documentId = (String) chunk.get("documentId");
        String chunkId = (String) chunk.get("chunkId");
        int totalChunks = ((Double) chunk.get("totalChunks")).intValue();
        String text = (String) chunk.get("text");

        Map<String, Object> unusedProperties = new HashMap<>();
        for (Map.Entry<String, Object> entry : chunk.entrySet()) {
            String key = entry.getKey();
            if (notPartOfCommonProperties(key)) {
                unusedProperties.put(key, entry.getValue());
            }
        }
        return Chunk.builder()
                .documentId(documentId)
                .chunkId(chunkId)
                .totalChunks(totalChunks)
                .text(text)
                .properties(unusedProperties)
                .build();
    }


    private static boolean notPartOfCommonProperties(String key) {
        return !key.equals("documentId") &&
                !key.equals("chunkId") &&
                !key.equals("totalChunks") &&
                !key.equals("text") &&
                !key.equals("_additional");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> parseData(GraphQLResponse response) {
        Object data = response.getData();
        if (data instanceof Map<?,?>) {
            return (Map<String,Object>) data;
        } else {
            throw new WeaviateException("Expected data to be a Map<String,Object> but was " + data.getClass());
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String,Object> parseGet(Map<String,Object> data) {
        Object get = data.get("Get");
        if (get instanceof Map<?,?>) {
            return (Map<String,Object>) get;
        } else {
            throw new WeaviateException("Expected get to be a Map<String,Object> but was " + get.getClass());
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String,Object>> parseChunks(Map<String,Object> get) {
        Object chunks = get.get(WeaviateContants.CLASS_NAME);
        if (chunks instanceof List<?>) {
            return (List<Map<String,Object>>) chunks;
        } else {
            throw new WeaviateException("Expected chunks to be a List<Map<String,Object>> but was " + chunks.getClass());
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String,Object> parseAdditional(Map<String,Object> _additional) {
        Object additional = _additional.get("_additional");
        if (additional instanceof Map<?,?>) {
            return (Map<String,Object>) additional;
        } else if (null != additional) {
            throw new WeaviateException("Expected additional to be a Map<String,Object> but was " + additional.getClass());
        } else {
            throw new WeaviateException("Expected additional to be a Map<String,Object> but was null");
        }
    }
}
