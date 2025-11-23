package org.rag4j.rag.generation.quality;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AnswerQualityServiceTest {

    @Test
    public void testSplitStringHappyPathAnswerToQuestion() {
        String input = "5 - The answer is correct and complete.";
        AnswerToQuestionQuality result = AnswerQualityService.splitString(input, AnswerToQuestionQuality.class);
        assertEquals(5, result.getQuality());
        assertEquals("The answer is correct and complete.", result.getReason());
    }

    @Test
    public void testSplitStringHappyPathAnswerFromContext() {
        String input = "5 - The answer is correct and complete.";
        AnswerFromContextQuality result = AnswerQualityService.splitString(input, AnswerFromContextQuality.class);
        assertEquals(5, result.getQuality());
        assertEquals("The answer is correct and complete.", result.getReason());
    }

    @Test
    public void testSplitStringNoDash() {
        String input = "The answer is correct and complete.";
        assertThrows(IllegalArgumentException.class, () ->
                AnswerQualityService.splitString(input, AnswerToQuestionQuality.class));
    }

    @Test
    public void testSplitStringEmptyParts() {
        String input = " - ";
        assertThrows(IllegalArgumentException.class, () ->
                AnswerQualityService.splitString(input, AnswerFromContextQuality.class));
    }

    @Test
    public void testSplitStringNonNumericScore() {
        String input = "Five - The answer is correct and complete.";
        assertThrows(IllegalArgumentException.class, () ->
                AnswerQualityService.splitString(input, AnswerFromContextQuality.class));
    }
}