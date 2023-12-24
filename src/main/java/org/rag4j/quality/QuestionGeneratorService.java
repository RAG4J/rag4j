package org.rag4j.quality;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.*;
import org.rag4j.openai.OpenAIConstants;
import org.rag4j.retrieval.Retriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;




public class QuestionGeneratorService {
    private final static Logger LOGGER = LoggerFactory.getLogger(QuestionGeneratorService.class);
    private final OpenAIClient client;

    private final Retriever retriever;


    public QuestionGeneratorService(OpenAIClient client, Retriever retriever) {
        this.client = client;
        this.retriever = retriever;
    }

    public void generateQuestionAnswerPairs(String fileName) {
        String directory = System.getProperty("user.dir") + "/src/main/resources/data"; // get current working directory
        Path filePath = Paths.get(directory, fileName);

        try (PrintWriter writer = new PrintWriter(filePath.toFile(), StandardCharsets.UTF_8)) {
            writer.println("document,chunk,text,question");
            retriever.loopOverChunks(chunk -> {
                // Use LLM to generate a question for this chunk
                String question = generateQuestion(chunk.getText());

                // Write the question and answer to a file
                String documentId = chunk.getDocumentId();
                String chunkId = String.valueOf(chunk.getChunkId());
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\"%n", documentId, chunkId, chunk.getText(), question);
                LOGGER.info("Generated question: {}", question);
            });
        } catch (IOException e) {
            LOGGER.error("An error occurred while writing to the file.", e);
            throw new RuntimeException(e);
        }
    }


    public String generateQuestion(String text) {
        List<ChatRequestMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatRequestSystemMessage(
                "You are a content writer reading a text and writing questions that are answered in that text. " +
                        "Use the context as provided and nothing else to come up with one question. The question should " +
                        "be a question that a person that does not know a lot about the context could ask. Do not use " +
                        "names in your question or exact dates. Do not use the exact words in the context. Each question " +
                        "must be one sentence only end always end with a '?' character. The context is provided after " +
                        "'context:'. The result should only contain the generated question, nothing else."));
        String contextMessage = String.format("Context: %s%nGenerated question:", text);
        chatMessages.add(new ChatRequestUserMessage(contextMessage));

        ChatCompletions chatCompletions = client.getChatCompletions(OpenAIConstants.GPT4, new ChatCompletionsOptions(chatMessages));

        return chatCompletions.getChoices().getFirst().getMessage().getContent();
    }

}
