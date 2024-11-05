package org.rag4j.rag.generation.knowledge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rag4j.rag.generation.chat.ChatPrompt;
import org.rag4j.rag.generation.chat.ChatService;

import java.util.List;
import java.util.Map;

public class KnowledgeExtractorService {
    private final ChatService chatService;

    public KnowledgeExtractorService(ChatService chatService) {
        this.chatService = chatService;
    }

    public List<Knowledge> extractKnowledge(String context) {
        ChatPrompt prompt = ChatPrompt.builder()
                .systemMessage(extractorSystemPrompt)
                .userMessage(extractorUserPrompt)
                .userParams(List.of(context))
                .build();

        String content = chatService.askForJsonResponse(prompt);
        System.out.println(content);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<Knowledge>> knowledgeMap = null;
        try {
            knowledgeMap = objectMapper.readValue(content, new TypeReference<Map<String, List<Knowledge>>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return knowledgeMap.get("knowledge_chunks");
    }

    // @formatter:off
    private static final String extractorUserPrompt =
            "Task: Extract Knowledge Chunks\n" +
            "\n" +
            "Objective: Extract meaningful knowledge chunks from the provided text. Each chunk should be a distinct, self-contained unit of information presented in a subject-description format. It is essential that the information is from the input text. Do not make any assumptions or add any external information.\n" +
            "\n" +
            "Instructions:\n" +
            "1. Identify distinct, relevant pieces of information from the text.\n" +
            "2. Ensure each piece of information focuses on one specific aspect: a person, an event, a location, an activity, a product, a concept, or a term.\n" +
            "3. Consolidate related pieces of information into broader categories only if they contribute to a clearer understanding of the subject.\n" +
            "4. For each piece of information, extract it as a \"subject\" and provide a corresponding detailed \"description\" taken from the input text. Do not include your own interpretation or additional information.\n" +
            "5. Ensure that the extracted chunks are formatted as a JSON object or array.\n" +
            "6. Provide enough context in the description to make each knowledge chunk understandable on its own.\n" +
            "\n" +
            "Format:\n" +
            "{{\n" +
            "    \"knowledge_chunks\": [\n" +
            "        {{\"subject\": \"subject\", \"description\": \"description\"}},\n" +
            "        ...\n" +
            "    ]\n" +
            "}}\n" +
            "Text: \n" +
            "%s\n";


    // @formatter:off
    private static final String extractorSystemPrompt =
            "You are an assistant that takes apart a piece of text into semantic chunks to be used in a RAG system.";
}
