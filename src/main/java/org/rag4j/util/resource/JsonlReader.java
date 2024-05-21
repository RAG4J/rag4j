package org.rag4j.util.resource;

import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads a JSONL file and returns a list of maps, where each map represents a line in the file. Beware the json can only
 * contain strings and does not support nested properties.
 */
@Getter
public class JsonlReader {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(JsonlReader.class);
    private List<Map<String,String>> lines;

    public JsonlReader(List<String> properties, String filename) {
        initLinesFromProvidedFile(properties, filename);
    }

    private void initLinesFromProvidedFile(List<String> properties, String filename) {
        this.lines = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getResourceAsStream("/data/" + filename);
            if (inputStream == null) {
                LOGGER.error("Error reading file: {}", filename);
                throw new ResourceException("Check the file name and try again.");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse each line as a JSON object
                JSONObject obj = new JSONObject(line);


                // Add each property to the map
                Map<String,String> lineMap = new HashMap<>();
                for (String property : properties) {
                    if (obj.get(property) instanceof JSONArray) {
                        List<String> values = new ArrayList<>();
                        for (int i = 0; i < obj.getJSONArray(property).length(); i++) {
                            values.add(obj.getJSONArray(property).getString(i));
                        }
                        lineMap.put(property, String.join(", ", values));
                    } else {
                        lineMap.put(property, obj.getString(property));
                    }
                }

                lines.add(lineMap);
            }
        } catch (IOException e) {
            LOGGER.error("Error reading file: {}", e.getMessage());
            throw new ResourceException(e.getMessage());
        }
    }
}
