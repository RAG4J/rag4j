package org.rag4j.rag.generation.quality;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents the quality of an answer from the provided context. The quality is a number between 1 and 5, where 5
 * means that the answer is completely generated from the provided context and 1 means there is no relationship
 * between the answer and the context. In the language of LLMs this is called hallucination.
 */
@Getter
@AllArgsConstructor
@ToString
public class AnswerFromContextQuality {
    private int quality;
    private String reason;
}
