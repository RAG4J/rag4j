package org.rag4j.indexing.splitters;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import org.rag4j.indexing.InputDocument;
import org.rag4j.indexing.Splitter;
import org.rag4j.rag.model.Chunk;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This sentence splitter makes of the OpenNLP sentence splitter. It does not work really good if you ask me. We might
 * need a better one that knows how to distill a title using spaces or enters for instance.
 */
public class SentenceSplitter implements Splitter {

    @Override
    public List<Chunk> split(InputDocument inputDocument, Chunk parentChunk) {
        String textToSplit = parentChunk != null ? parentChunk.getText() : inputDocument.getText();
        SentenceDetectorME sentenceDetector = createSentenceDetector();
        String[] sentences = sentenceDetector.sentDetect(textToSplit);

        List<Chunk> chunks = new ArrayList<>();
        for (int i = 0; i < sentences.length; i++) {
            String chunkSizeStr = String.valueOf(i);
            String chunkId = parentChunk != null ? parentChunk.getChunkId() + "_" + chunkSizeStr : chunkSizeStr;

            chunks.add(Chunk.builder()
                    .documentId(inputDocument.getDocumentId())
                    .chunkId(chunkId)
                    .totalChunks(sentences.length)
                    .text(sentences[i])
                    .properties(inputDocument.getProperties())
                    .build());
        }

        return chunks;
    }

    private SentenceDetectorME createSentenceDetector() {
        String sentenceModelFilePath = "/opennlp/opennlp-en-ud-ewt-sentence-1.0-1.9.3.bin";
        try (InputStream is = getClass().getResourceAsStream(sentenceModelFilePath)) {
            if (is == null) {
                throw new FileNotFoundException("Could not find file: " + sentenceModelFilePath);
            }
            return new SentenceDetectorME(new SentenceModel(is));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
