package org.rag4j.rag.generation.quality;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents the overall quality of an answer. Two factors are used:
 * <ul>
 *     <li>Is the provided answer a complete answer to the provided question</li>
 *     <li>Is the provided answer limited to the context provided, or does it hallucinate.</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
@ToString
public class AnswerQuality {
    private AnswerToQuestionQuality answerToQuestionQuality;
    private AnswerFromContextQuality answerFromContextQuality;
}
