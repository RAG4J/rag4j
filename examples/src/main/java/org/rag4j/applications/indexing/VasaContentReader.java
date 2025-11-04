package org.rag4j.applications.indexing;

import org.rag4j.indexing.InputDocument;
import org.rag4j.indexing.ContentReader;
import org.rag4j.util.resource.JsonlReader;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

public class VasaContentReader implements ContentReader {

    @Override
    public Stream<InputDocument> read() {
        String filename = "vasa-timeline.jsonl";
        JsonlReader jsonlReader = new JsonlReader(List.of("title", "timerange", "body"), filename);
        return jsonlReader.getLines().stream().map(line -> {
            Map<String, Object> properties = new HashMap<>();
            properties.put("title", line.get("title"));
            properties.put("timerange", line.get("timerange"));
            String documentId = line.get("title").toLowerCase(Locale.ROOT).replace(" ", "-");
            return InputDocument.builder()
                    .documentId(documentId)
                    .text(line.get("body"))
                    .properties(properties)
                    .build();
        });
    }
}
