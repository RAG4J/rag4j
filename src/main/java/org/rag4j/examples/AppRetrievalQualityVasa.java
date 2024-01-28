package org.rag4j.examples;

import org.rag4j.indexing.Embedder;
import org.rag4j.indexing.IndexingService;
import org.rag4j.indexing.OpenNLPSentenceSplitter;
import org.rag4j.localembedder.AllMiniLmL6V2QEmbedder;
import org.rag4j.openai.OpenAIEmbedder;
import org.rag4j.quality.QuestionAnswerRecord;
import org.rag4j.quality.RetrievalQuality;
import org.rag4j.quality.RetrievalQualityService;
import org.rag4j.retrieval.Retriever;
import org.rag4j.store.InternalContentStore;
import org.rag4j.util.KeyLoader;
import org.rag4j.weaviate.WeaviateAccess;
import org.rag4j.weaviate.retrieval.WeaviateRetriever;

import java.util.List;

/**
 * This uses the stored Chunks and vectors in Weaviate to test the quality of the retrieval.
 */
public class AppRetrievalQualityVasa {


    public static void printQuality(Retriever retriever, Embedder embedder) {
        RetrievalQualityService retrievalQualityService = new RetrievalQualityService(retriever);
        List<QuestionAnswerRecord> questionAnswerRecords =
                retrievalQualityService.readQuestionAnswersFromFile("/data/questions_answers.txt");

        RetrievalQuality retrievalQuality = retrievalQualityService.obtainRetrievalQuality(questionAnswerRecords, embedder);

        System.out.printf("Precision: %.3f%n", retrievalQuality.getPrecision());
        System.out.printf("Total items: %d%n", retrievalQuality.totalItems());

    }

    public static void main(String[] args) {

        Embedder embedder = new AllMiniLmL6V2QEmbedder();
        InternalContentStore contentStore = new InternalContentStore(embedder);
        IndexingService indexingService = new IndexingService(contentStore);
        indexingService.indexDocuments(new VasaContentReader(), new OpenNLPSentenceSplitter());

        System.out.println("Use the internal embedder, and the internal content store");
        printQuality(contentStore, embedder);

        System.out.println("Use the OpenAI embedder, and the Weaviate content store");
        KeyLoader keyLoader = new KeyLoader();
        embedder = new OpenAIEmbedder(keyLoader);
        WeaviateAccess weaviateAccess = new WeaviateAccess(keyLoader);
        Retriever retriever = new WeaviateRetriever(weaviateAccess, embedder);
        printQuality(retriever, embedder);
    }
}
