package org.rag4j.rag.retrieval.quality;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Value object that contains the id of the document where the chunk is coming from, the id of the chunk, the text of
 * the chunk and a question about the chunk. The class is used to read a file with questions that match the chunk. So
 * the chunk is the answer. These records can be used to evaluate the quality of the retrieval.
 */
@Getter
@AllArgsConstructor
@Builder
public class QuestionAnswerRecord {
    private String documentId;
    private String chunkId;
    private String text;
    private String question;
}
