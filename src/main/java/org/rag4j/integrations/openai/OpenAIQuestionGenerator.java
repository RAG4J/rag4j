package org.rag4j.integrations.openai;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.*;
import org.rag4j.rag.generation.QuestionGenerator;
import org.rag4j.util.keyloader.KeyLoader;

import java.util.ArrayList;
import java.util.List;

public class OpenAIQuestionGenerator implements QuestionGenerator {
    private final OpenAIClient client;
    private final String model;

    public OpenAIQuestionGenerator() {
        this(new KeyLoader(), OpenAIConstants.DEFAULT_MODEL);
    }

    public OpenAIQuestionGenerator(KeyLoader keyLoader, String model) {
        this.client = OpenAIFactory.obtainsClient(keyLoader.getOpenAIKey());
        this.model = model;
    }

    @Override
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

        ChatCompletions chatCompletions = client.getChatCompletions(this.model, new ChatCompletionsOptions(chatMessages));

        return chatCompletions.getChoices().getFirst().getMessage().getContent();
    }
}
