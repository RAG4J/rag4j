package org.rag4j.util.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JsonlReaderTest {

    private JsonlReader jsonlReader;

    @BeforeEach
    void setUp() {
        List<String> properties = List.of(
                "speakers",
                "title",
                "description",
                "room",
                "time",
                "tags"
        );
        jsonlReader = new JsonlReader(properties, "sessions.jsonl");
    }

    @Test
    @DisplayName("Should read lines from provided file")
    void shouldReadLinesFromProvidedFile() throws IOException {
        List<Map<String, String>> lines = jsonlReader.getLines();
        assertEquals(1, lines.size());
        assertEquals("The Art of Questions: Creating a Semantic Search-Based Question-Answering System with LLMs", lines.getFirst().get("title"));
        assertEquals("Jettro Coenradie, Daniël Spee", lines.getFirst().get("speakers"));
    }

}