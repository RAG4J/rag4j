package org.rag4j.quality;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.*;
import org.rag4j.chat.ChatPrompt;
import org.rag4j.chat.ChatService;
import org.rag4j.openai.OpenAIChatService;
import org.rag4j.openai.OpenAIConstants;
import org.rag4j.openai.OpenAIPrompt;
import org.rag4j.tracker.RAGObserver;

import java.util.List;

/**
 * Service used to determine the quality of an answer. The service uses the OpenAI API to verify the generated answer
 * using two techniques:
 * <ul>
 *     <li>Answer to question: The answer is verified against the question. The answer is scored between 1 and 5. 5
 *     means the answer contains the answer to the proposed question completely. 1 means there is not match between
 *     the answer and the question at all.</li>
 *     <li>Answer from context: The answer is verified against the provided context. The answer is scored between 1 and
 *     5. 5 means the answer contains only facts from the context. 1 means there is not match between the answer and
 *     the provided context at all. If the answer contains exact phrases from the context, the score should be lower
 *     as well.</li>
 * </ul>
 * The class contains two main methods to ask for the separate scores. The method {@link #determineQualityOfAnswer(RAGObserver)}
 * combines the two scores into one object.
 */
public class AnswerQualityService {
    private final ChatService chatService;

    public AnswerQualityService(ChatService chatService) {
        this.chatService = chatService;
    }

    public AnswerQuality determineQualityOfAnswer(RAGObserver ragObserver) {
        AnswerToQuestionQuality answerToQuestionQuality = determineQualityOfAnswerToQuestion(ragObserver);
        AnswerFromContextQuality answerFromContextQuality = determineQualityAnswerFromContext(ragObserver);

        return new AnswerQuality(answerToQuestionQuality, answerFromContextQuality);
    }

    public AnswerToQuestionQuality determineQualityOfAnswerToQuestion(RAGObserver ragObserver) {
        String answer = ragObserver.getAnswer();
        String question = ragObserver.getQuestion();

        ChatPrompt prompt = ChatPrompt.builder()
                .systemMessageFilename("/quality/quality_of_answer_to_question_system.txt")
                .userMessageFilename("/quality/quality_of_answer_to_question_user.txt")
                .userParams(List.of(question, answer))
                .build();

        String content = chatService.askForQuality(prompt);

        return splitString(content, AnswerToQuestionQuality.class);
    }

    public AnswerFromContextQuality determineQualityAnswerFromContext(RAGObserver ragObserver) {
        String answer = ragObserver.getAnswer();
        String context = ragObserver.getContext();

        ChatPrompt prompt = ChatPrompt.builder()
                .systemMessageFilename("/quality/quality_of_answer_from_context_system.txt")
                .userMessageFilename("/quality/quality_of_answer_from_context_user.txt")
                .userParams(List.of(answer, context))
                .build();

        String content = chatService.askForQuality(prompt);

        return splitString(content, AnswerFromContextQuality.class);
    }

    /**
     * Splits the input string into a quality and a reason. The input string is expected to be in the format of
     * <code>quality - reason</code>. The quality is expected to be an integer. The reason is expected to be a string.
     * @param input The input string to split
     * @param clazz The class to cast the result to
     * @return The split string as an object of the provided class
     * @param <T> The type of the class to cast the result to
     */
    static <T>T splitString(String input, Class<T> clazz) {
        if (input == null || !input.contains("-")) {
            throw new IllegalArgumentException("Input string is not in the correct format");
        }

        String[] parts = input.split("-", 2);

        if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new IllegalArgumentException("Input string is not in the correct format");
        }

        parts[0] = parts[0].trim();
        parts[1] = parts[1].trim();

        if (!parts[0].matches("\\d+")) {
            throw new IllegalArgumentException("Input string is not in the correct format");
        }

        int quality = Integer.parseInt(parts[0]);
        String reason = parts[1];

        if (clazz == AnswerToQuestionQuality.class) {
            return clazz.cast(new AnswerToQuestionQuality(quality, reason));
        } else if (clazz == AnswerFromContextQuality.class) {
            return clazz.cast(new AnswerFromContextQuality(quality, reason));
        } else {
            throw new IllegalArgumentException("Unsupported class type");
        }
    }
}
