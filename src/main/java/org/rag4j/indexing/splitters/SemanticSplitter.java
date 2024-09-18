package org.rag4j.indexing.splitters;

import org.rag4j.indexing.InputDocument;
import org.rag4j.indexing.Splitter;
import org.rag4j.rag.generation.knowledge.Knowledge;
import org.rag4j.rag.generation.knowledge.KnowledgeExtractorService;
import org.rag4j.rag.model.Chunk;

import java.util.ArrayList;
import java.util.List;

public class SemanticSplitter implements Splitter {
    private final KnowledgeExtractorService knowledgeExtractorService;

    public SemanticSplitter(KnowledgeExtractorService knowledgeExtractorService) {
        this.knowledgeExtractorService = knowledgeExtractorService;
    }

    @Override
    public List<Chunk> split(InputDocument inputDocument, Chunk parentChunk) {
        String textToSplit = parentChunk != null ? parentChunk.getText() : inputDocument.getText();
        List<Knowledge> knowledgeList = knowledgeExtractorService.extractKnowledge(textToSplit);

        List<Chunk> chunks = new ArrayList<>();
        for (int i = 0; i < knowledgeList.size(); i++) {
            String chunkSizeStr = String.valueOf(i);
            String chunkId = parentChunk != null ? parentChunk.getChunkId() + "_" + chunkSizeStr : chunkSizeStr;

            String semanticText = knowledgeList.get(i).getSubject() + ": " + knowledgeList.get(i).getDescription();

            chunks.add(Chunk.builder()
                    .documentId(inputDocument.getDocumentId())
                    .chunkId(chunkId)
                    .totalChunks(knowledgeList.size())
                    .text(semanticText)
                    .properties(inputDocument.getProperties())
                    .build());
        }

        return chunks;
    }
}
