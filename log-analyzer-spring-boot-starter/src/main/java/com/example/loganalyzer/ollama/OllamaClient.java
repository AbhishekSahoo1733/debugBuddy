package com.example.loganalyzer.ollama;

import com.example.loganalyzer.config.LogAnalyzerProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class OllamaClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final LogAnalyzerProperties properties;

    public OllamaClient(ObjectMapper objectMapper, LogAnalyzerProperties properties) {
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(properties.getTimeoutMs()))
                .build();
    }

    public String analyze(String prompt) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", properties.getModel());
        body.put("prompt", prompt);
        body.put("stream", false);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(properties.getOllamaUrl() + "/api/generate"))
                    .timeout(Duration.ofMillis(properties.getTimeoutMs()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                return "Ollama call failed: HTTP " + response.statusCode() + " body=" + response.body();
            }

            Map<String, Object> data = objectMapper.readValue(response.body(), new TypeReference<>() {
            });
            Object result = data.get("response");
            return result == null ? "Ollama returned empty response." : result.toString();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Ollama call interrupted: " + e.getMessage();
        } catch (IOException e) {
            return "Ollama call failed: " + e.getMessage();
        }
    }
}
