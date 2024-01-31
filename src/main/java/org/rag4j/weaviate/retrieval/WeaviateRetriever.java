package org.rag4j.weaviate.retrieval;

import io.weaviate.client.base.Result;
import io.weaviate.client.v1.filters.Operator;
import io.weaviate.client.v1.filters.WhereFilter;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import io.weaviate.client.v1.graphql.query.argument.NearVectorArgument;
import io.weaviate.client.v1.graphql.query.argument.WhereArgument;
import io.weaviate.client.v1.graphql.query.fields.Field;
import org.rag4j.domain.Chunk;
import org.rag4j.domain.RelevantChunk;
import org.rag4j.indexing.Embedder;
import org.rag4j.retrieval.ChunkProcessor;
import org.rag4j.retrieval.Retriever;
import org.rag4j.weaviate.WeaviateAccess;
import org.rag4j.weaviate.WeaviateException;

import java.util.List;

import static org.rag4j.weaviate.WeaviateContants.CLASS_NAME;
import static org.rag4j.weaviate.retrieval.WeaviateResponseParser.parseGraphQLRelevantResponse;
import static org.rag4j.weaviate.retrieval.WeaviateResponseParser.parseGraphQLResponseList;

public class WeaviateRetriever implements Retriever {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(WeaviateRetriever.class);

    private final WeaviateAccess weaviateAccess;
    private final Embedder embedder;

    public WeaviateRetriever(WeaviateAccess weaviateAccess, Embedder embedder) {
        this.weaviateAccess = weaviateAccess;
        this.embedder = embedder;
    }

    @Override
    public List<RelevantChunk> findRelevantChunks(String question, int maxResults) {
        return findRelevantChunks(question, embedder.embed(question), maxResults);
    }

    @Override
    public List<RelevantChunk> findRelevantChunks(String question, List<Double> vector, int maxResults) {
        Float[] floatVector = vector.stream()
                .map(Double::floatValue)
                .toArray(Float[]::new);

        Result<GraphQLResponse> result = weaviateAccess.getClient().graphQL().get()
                .withClassName(CLASS_NAME)
                .withFields(
                        Field.builder().name("text").build(),
                        Field.builder().name("documentId").build(),
                        Field.builder().name("chunkId").build(),
                        Field.builder().name("totalChunks").build(),
                        Field.builder().name("_additional").fields(
                                Field.builder().name("distance").build()
                        ).build()
                )
                .withNearVector(NearVectorArgument.builder()
                        .vector(floatVector)
                        .build())
                .withLimit(maxResults)
                .run();
        if (result.getError() != null) {
            LOGGER.error("Error: {}", result.getError().getMessages());
            throw new WeaviateException("Error: " + result.getError().getMessages());
        }

        GraphQLResponse response = result.getResult();
        return parseGraphQLRelevantResponse(response);
    }

    @Override
    public Chunk getChunk(String documentId, int chunkId) {

        Result<GraphQLResponse> result = weaviateAccess.getClient().graphQL().get()
                .withClassName(CLASS_NAME)
                .withFields(
                        Field.builder().name("text").build(),
                        Field.builder().name("documentId").build(),
                        Field.builder().name("chunkId").build(),
                        Field.builder().name("totalChunks").build()
                )
                .withWhere(WhereArgument.builder()
                        .filter(WhereFilter.builder()
                                .operator(Operator.And)
                                .operands(
                                        WhereFilter.builder()
                                                .path("documentId")
                                                .operator(Operator.Equal)
                                                .valueText(documentId)
                                                .build(),
                                        WhereFilter.builder()
                                                .path("chunkId")
                                                .operator(Operator.Equal)
                                                .valueInt(chunkId)
                                                .build()
                                )
                                .build())
                        .build()
                )
                .run();
        if (result.getError() != null) {
            LOGGER.error("Error: {}", result.getError().getMessages());
            throw new WeaviateException("Error: " + result.getError().getMessages());
        }

        GraphQLResponse response = result.getResult();
        return parseGraphQLResponse(response);
    }

    @Override
    public void loopOverChunks(ChunkProcessor chunkProcessor) {
        int limit = 100;
        int offset = 0;
        boolean done = false;
        while (!done) {
            Result<GraphQLResponse> result = weaviateAccess.getClient().graphQL().get()
                    .withClassName(CLASS_NAME)
                    .withFields(
                            Field.builder().name("text").build(),
                            Field.builder().name("documentId").build(),
                            Field.builder().name("chunkId").build(),
                            Field.builder().name("totalChunks").build()
                    )
                    .withLimit(limit)
                    .withOffset(offset)
                    .run();
            if (result.getError() != null) {
                LOGGER.error("Error: {}", result.getError().getMessages());
                throw new WeaviateException("Error: " + result.getError().getMessages());
            }

            GraphQLResponse response = result.getResult();
            List<Chunk> chunks = parseGraphQLResponseList(response);
            for (Chunk chunk : chunks) {
                chunkProcessor.process(chunk);
            }

            if (chunks.size() < limit) {
                done = true;
            } else {
                offset += limit;
            }
        }
    }

    private Chunk parseGraphQLResponse(GraphQLResponse response) {
        return parseGraphQLResponseList(response).getFirst();
    }
}
