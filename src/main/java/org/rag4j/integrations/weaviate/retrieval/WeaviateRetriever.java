package org.rag4j.integrations.weaviate.retrieval;

import io.weaviate.client.base.Result;
import io.weaviate.client.v1.filters.Operator;
import io.weaviate.client.v1.filters.WhereFilter;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import io.weaviate.client.v1.graphql.query.Get;
import io.weaviate.client.v1.graphql.query.argument.FusionType;
import io.weaviate.client.v1.graphql.query.argument.HybridArgument;
import io.weaviate.client.v1.graphql.query.argument.NearVectorArgument;
import io.weaviate.client.v1.graphql.query.argument.WhereArgument;
import io.weaviate.client.v1.graphql.query.fields.Field;
import org.rag4j.integrations.weaviate.WeaviateAccess;
import org.rag4j.integrations.weaviate.WeaviateException;
import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.model.RelevantChunk;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.rag.retrieval.ChunkProcessor;
import org.rag4j.rag.retrieval.Retriever;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.rag4j.integrations.weaviate.WeaviateContants.CLASS_NAME;
import static org.rag4j.integrations.weaviate.retrieval.WeaviateResponseParser.parseGraphQLRelevantResponse;
import static org.rag4j.integrations.weaviate.retrieval.WeaviateResponseParser.parseGraphQLResponseList;

public class WeaviateRetriever implements Retriever {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(WeaviateRetriever.class);

    private final WeaviateAccess weaviateAccess;
    private final Embedder embedder;
    private final Boolean useHybrid;
    private final List<String> fieldsToRetrieve;

    public WeaviateRetriever(WeaviateAccess weaviateAccess, Embedder embedder) {
        this(weaviateAccess, embedder, false, List.of());
    }

    public WeaviateRetriever(WeaviateAccess weaviateAccess, Embedder embedder, Boolean useHybrid) {
        this(weaviateAccess, embedder, useHybrid, List.of());
    }

    public WeaviateRetriever(WeaviateAccess weaviateAccess, Embedder embedder, Boolean useHybrid, List<String> fieldsToRetrieve) {
        this.weaviateAccess = weaviateAccess;
        this.embedder = embedder;
        this.useHybrid = useHybrid;
        this.fieldsToRetrieve = fieldsToRetrieve;
    }


    @Override
    public List<RelevantChunk> findRelevantChunks(String question, int maxResults) {
        return findRelevantChunks(question, embedder.embed(question), maxResults);
    }

    @Override
    public List<RelevantChunk> findRelevantChunks(String question, List<Float> vector, int maxResults) {
        Float[] floatVector = vector.toArray(new Float[0]);

        Get get = weaviateAccess.getClient().graphQL().get()
                .withClassName(CLASS_NAME)
                .withFields(buildFields(true))
                .withLimit(maxResults);
        if (useHybrid) {
            get.withHybrid(HybridArgument.builder()
                    .query(question)
                    .properties(fieldsToRetrieve.toArray(new String[0]))
                    .vector(floatVector)
                    .alpha(0.5f)
                    .fusionType(FusionType.RANKED)
                    .build());
        } else {
            get.withNearVector(NearVectorArgument.builder()
                    .vector(floatVector)
                    .certainty(0.5f)
                    .build());
        }

        Result<GraphQLResponse> result = get.run();

        if (result.getError() != null) {
            LOGGER.error("Error: {}", result.getError().getMessages());
            throw new WeaviateException("Error: " + result.getError().getMessages());
        }

        GraphQLResponse response = result.getResult();
        return parseGraphQLRelevantResponse(response);
    }

    @Override
    public Chunk getChunk(String documentId, String chunkId) {

        Result<GraphQLResponse> result = weaviateAccess.getClient().graphQL().get()
                .withClassName(CLASS_NAME)
                .withFields(buildFields(false))
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
                                                .valueString(chunkId)
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
                    .withFields(buildFields(false))
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

    private Field[] buildFields(boolean withAdditionalFields) {
        List<Field> fields = new ArrayList<>(List.of(
                Field.builder().name("text").build(),
                Field.builder().name("documentId").build(),
                Field.builder().name("chunkId").build(),
                Field.builder().name("totalChunks").build()));
        if (withAdditionalFields) {
            fields.add(Field.builder().name("_additional").fields(
                    Field.builder().name("distance").build(),
                    Field.builder().name("score").build()
            ).build());
        }
        fields.addAll(fieldsToRetrieve.stream()
                .map(fieldName -> Field.builder().name(fieldName).build())
                .toList()
        );

        return fields.toArray(new Field[0]);
    }

    private Chunk parseGraphQLResponse(GraphQLResponse response) {
        return parseGraphQLResponseList(response).getFirst();
    }
}
