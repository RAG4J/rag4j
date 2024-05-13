package org.rag4j.integrations.ollama;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class OllamaAccess {
    private final String connectionUrl;

    public OllamaAccess() {
        this("http://localhost:11434");
    }

    public OllamaAccess(String protocol, String host, int port) {
        this(String.format("%s://%s:%d", protocol, host, port));
    }

    public OllamaAccess(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public List<String> listModels() {
        String connectionUrl = this.connectionUrl + "/api/tags";
        List<String> models = new ArrayList<>();


        JSONObject json = execute_get_request(connectionUrl);
        JSONArray jsonArray = json.getJSONArray("models");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject model = jsonArray.getJSONObject(i);
            JSONObject details = model.getJSONObject("details");
            String modelInfo = model.getString("name") + " ( " +
                    details.getString("parameter_size") + " - " +
                    details.getString("quantization_level") + " )";
            models.add(modelInfo);
        }

        return models;
    }

    public String generateAnswer(String prompt, String model) {
        String connectionUrl = this.connectionUrl + "/api/generate";

        // Create JSON request body
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("prompt", prompt);
        jsonRequest.put("model", model);
        jsonRequest.put("stream", false);

        JSONObject jsonResponse = execute_post_request(connectionUrl, jsonRequest);

        return jsonResponse.getString("response");
    }

    public List<Float> generateEmbedding(String text, String model) {
        String connectionUrl = this.connectionUrl + "/api/embeddings";

        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("prompt", text);
        jsonRequest.put("model", model);
        execute_post_request(connectionUrl, jsonRequest);

        JSONObject jsonResponse = execute_post_request(connectionUrl, jsonRequest);

        JSONArray jsonArray = jsonResponse.getJSONArray("embedding");
        List<Float> embeddings = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            embeddings.add(jsonArray.getFloat(i));
        }
        return embeddings;
    }

    private JSONObject execute_get_request(String connectionUrl) {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(connectionUrl))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new AccessOllamaException(e.getMessage(), e);
        }

        if (response.statusCode() == 200) {
            return new JSONObject(response.body());
        } else {
            throw new AccessOllamaException("Error executing get request: " + response.body());
        }
    }

    private JSONObject execute_post_request(String connectionUrl, JSONObject jsonRequest) {
        String requestBody = jsonRequest.toString();

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(connectionUrl))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .header("Content-Type", "application/json")
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new AccessOllamaException(e.getMessage(), e);
        }

        if (response.statusCode() == 200) {
            return new JSONObject(response.body());
        } else {
            throw new AccessOllamaException("Error generating embedding: " + response.body());
        }
    }
}
