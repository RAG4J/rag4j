package org.rag4j.applications.retrieval;

import com.openai.client.OpenAIClient;
import org.rag4j.applications.indexing.VasaContentReader;
import org.rag4j.integration.openai.OpenAIFactory;
import org.rag4j.local.store.InternalContentStore;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.indexing.IndexingService;
import org.rag4j.indexing.splitters.SentenceSplitter;
import org.rag4j.rag.embedding.local.OnnxBertEmbedder;
import org.rag4j.integration.openai.OpenAIEmbedder;
import org.rag4j.rag.retrieval.quality.QuestionAnswerRecord;
import org.rag4j.rag.retrieval.quality.RetrievalQuality;
import org.rag4j.rag.retrieval.quality.RetrievalQualityService;
import org.rag4j.rag.retrieval.Retriever;
import org.rag4j.util.keyloader.KeyLoader;
import org.rag4j.integration.weaviate.WeaviateAccess;
import org.rag4j.integration.weaviate.retrieval.WeaviateRetriever;

import java.util.List;

/**
 * The goal for this application is to show how to determine the quality of a retriever. Both the internal content store
 * and the Weaviate content store are used to demonstrate the quality of the retriever. When working with the Weaviate
 * content store, it is important to use the same embedder used during indexing.
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

        Embedder embedder = new OnnxBertEmbedder();
        InternalContentStore contentStore = new InternalContentStore(embedder);
        IndexingService indexingService = new IndexingService(contentStore);
        indexingService.indexDocuments(new VasaContentReader(), new SentenceSplitter());

        System.out.println("Use the internal embedder, and the internal content store");
        printQuality(contentStore, embedder);

        System.out.println("Use the OpenAI embedder, and the Weaviate content store");
        KeyLoader keyLoader = new KeyLoader();
        OpenAIClient openAIClient = OpenAIFactory.obtainsClient(keyLoader.getOpenAIKey());
        embedder = new OpenAIEmbedder(openAIClient);
        WeaviateAccess weaviateAccess = new WeaviateAccess(keyLoader);
        Retriever retriever = new WeaviateRetriever(weaviateAccess, embedder);
        printQuality(retriever, embedder);
    }
}
