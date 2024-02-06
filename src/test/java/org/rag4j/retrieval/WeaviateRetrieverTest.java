package org.rag4j.retrieval;

import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Response;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.graphql.GraphQL;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import io.weaviate.client.v1.graphql.query.Get;
import io.weaviate.client.v1.graphql.query.fields.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rag4j.domain.Chunk;
import org.rag4j.domain.RelevantChunk;
import org.rag4j.indexing.Embedder;
import org.rag4j.util.KeyLoader;
import org.rag4j.weaviate.WeaviateAccess;
import org.rag4j.weaviate.WeaviateException;
import org.rag4j.weaviate.retrieval.WeaviateResponseParser;
import org.rag4j.weaviate.retrieval.WeaviateRetriever;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class WeaviateRetrieverTest {

    @Mock
    private WeaviateAccess weaviateAccess;

    @Mock
    private WeaviateClient weaviateClient;

    @Mock
    private KeyLoader keyLoader;

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
        List<Double> vector = Arrays.asList(1.0, 2.0, 3.0);
        int maxResults = 5;

        Chunk chunk1 = new Chunk("doc1", 0, 3, "an answer", Map.of());
        Chunk chunk2 = new Chunk("doc1", 2, 3, "with a question", Map.of());

        RelevantChunk relevantChunk1 = new RelevantChunk(chunk1, 0.5d);
        RelevantChunk relevantChunk2 = new RelevantChunk(chunk2, 0.45d);

        List<RelevantChunk> expectedChunks = Arrays.asList(relevantChunk1, relevantChunk2);

        when(embedder.embed(question)).thenReturn(vector);
        GraphQLResponse response = GraphQLResponse.builder()
                .data(Map.of("Get", Map.of("Chunk", List.of(Map.of("documentId", "doc1", "chunkId", 0d, "totalChunks", 3d, "text", "an answer","_additional",Map.of("distance",0.5d)),
                        Map.of("documentId", "doc1", "chunkId", 2d, "totalChunks", 3d, "text", "with a question","_additional",Map.of("distance",0.45d))))))
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

//    @Test
//    public void findRelevantChunksThrowsExceptionWhenErrorOccurs() {
//        String question = "test question";
//        List<Double> vector = Arrays.asList(1.0, 2.0, 3.0);
//        int maxResults = 5;
//
//        when(embedder.embed(question)).thenReturn(vector);
//        when(weaviateAccess.getClient().graphQL().get().run().getError()).thenReturn(new Error());
//
//        assertThrows(WeaviateException.class, () -> weaviateRetriever.findRelevantChunks(question, maxResults));
//    }
//
//    @Test
//    public void getChunkReturnsExpectedChunk() {
//        String documentId = "testId";
//        int chunkId = 1;
//        Chunk expectedChunk = new Chunk();
//
//        when(weaviateAccess.getClient().graphQL().get().run().getResult()).thenReturn(new GraphQLResponse());
//        when(WeaviateResponseParser.parseGraphQLResponse(new GraphQLResponse())).thenReturn(expectedChunk);
//
//        Chunk actualChunk = weaviateRetriever.getChunk(documentId, chunkId);
//
//        assertEquals(expectedChunk, actualChunk);
//    }
//
//    @Test
//    public void getChunkThrowsExceptionWhenErrorOccurs() {
//        String documentId = "testId";
//        int chunkId = 1;
//
//        when(weaviateAccess.getClient().graphQL().get().run().getError()).thenReturn(new Error());
//
//        assertThrows(WeaviateException.class, () -> weaviateRetriever.getChunk(documentId, chunkId));
//    }
}