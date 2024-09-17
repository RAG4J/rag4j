package org.rag4j.integrations.weaviate.retrieval;

import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.graphql.GraphQL;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import io.weaviate.client.v1.graphql.query.Get;
import io.weaviate.client.v1.graphql.query.fields.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rag4j.integrations.weaviate.WeaviateAccess;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.model.RelevantChunk;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class WeaviateRetrieverTest {

    @Mock
    private WeaviateAccess weaviateAccess;

    @Mock
    private WeaviateClient weaviateClient;

    @Mock
    private Embedder embedder;

    @Mock
    GraphQL graphQL;

    @Mock
    Get get;

    @Mock
    Result<GraphQLResponse> mockResponse;

    private WeaviateRetriever weaviateRetriever;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        weaviateRetriever = new WeaviateRetriever(weaviateAccess, embedder, false);
    }

    @Test
    public void findRelevantChunksReturnsExpectedChunks() {
        String question = "test question";
        List<Float> vector = Arrays.asList(1.0f, 2.0f, 3.0f);
        int maxResults = 5;

        Chunk chunk1 = Chunk.builder()
                .documentId("doc1")
                .chunkId("0")
                .totalChunks(3)
                .text("an answer")
                .properties(Map.of())
                .build();
        Chunk chunk2 = Chunk.builder()
                .documentId("doc1")
                .chunkId("2")
                .totalChunks(3)
                .text("with a question")
                .properties(Map.of())
                .build();

        RelevantChunk relevantChunk1 = new RelevantChunk(chunk1, 0.5d);
        RelevantChunk relevantChunk2 = new RelevantChunk(chunk2, 0.45d);

        List<RelevantChunk> expectedChunks = Arrays.asList(relevantChunk1, relevantChunk2);

        when(embedder.embed(question)).thenReturn(vector);
        GraphQLResponse response = GraphQLResponse.builder()
                .data(Map.of("Get", Map.of("Chunk", List.of(Map.of("documentId", "doc1", "chunkId", "0", "totalChunks", 3d, "text", "an answer","_additional",Map.of("distance",0.5d)),
                        Map.of("documentId", "doc1", "chunkId", "2", "totalChunks", 3d, "text", "with a question","_additional",Map.of("distance",0.45d))))))
                .build();

        when(mockResponse.getResult()).thenReturn(response);
        when(get.withClassName("Chunk")).thenReturn(get);
        when(get.withFields(any(Field[].class))).thenReturn(get);
        when(get.withLimit(anyInt())).thenReturn(get);
        when(get.withNearVector(any())).thenReturn(get);
        when(get.withHybrid(any())).thenReturn(get);

        when(get.run()).thenReturn(mockResponse);
        when(graphQL.get()).thenReturn(get);
        when(weaviateClient.graphQL()).thenReturn(graphQL);
        when(weaviateAccess.getClient()).thenReturn(weaviateClient);

        List<RelevantChunk> actualChunks = weaviateRetriever.findRelevantChunks(question, maxResults);

        assertEquals(expectedChunks, actualChunks);
    }

}