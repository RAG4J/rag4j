package org.rag4j.rag.generation.quality;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents the quality of an answer to a question. The quality is a number between 1 and 5, where 5
 * means that the answer is a complete answer to the question and 1 means that the answer is not related to the
 * question at all.
 */
@Getter
@AllArgsConstructor
@ToString
@Builder
public class AnswerToQuestionQuality {
    private int quality;
    private String reason;

}
