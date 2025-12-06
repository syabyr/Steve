package com.steve.ai.ai;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.steve.ai.SteveMod;
import com.steve.ai.config.SteveConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Client for Ollama API - Local AI inference
 * FREE and PRIVATE - runs completely offline on your machine
 * Speed depends on your hardware (GPU highly recommended)
 * Default endpoint: http://localhost:11434
 */
public class OllamaClient {
    private final HttpClient client;
    private final String baseUrl;
    private final String model;

    public OllamaClient() {
        this.baseUrl = SteveConfig.OLLAMA_BASE_URL.get();
        this.model = SteveConfig.OLLAMA_MODEL.get();
        this.client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }

    public String sendRequest(String systemPrompt, String userPrompt) {
        String apiUrl = baseUrl + "/api/chat";
        SteveMod.LOGGER.info("[Ollama] Preparing request to {} with model {}", apiUrl, model);
        if (systemPrompt != null) {
            SteveMod.LOGGER.debug("[Ollama] System prompt length: {}", systemPrompt.length());
        }
        if (userPrompt != null) {
            SteveMod.LOGGER.debug("[Ollama] User prompt length: {}", userPrompt.length());
        }
        
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.addProperty("stream", false);
        
        JsonArray messages = new JsonArray();
        
        // System message
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", systemPrompt);
        messages.add(systemMessage);

        // User message
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", userPrompt);
        messages.add(userMessage);

        requestBody.add("messages", messages);
        
        // Add options for better control
        JsonObject options = new JsonObject();
        options.addProperty("temperature", SteveConfig.TEMPERATURE.get());
        options.addProperty("num_predict", SteveConfig.MAX_TOKENS.get());
        requestBody.add("options", options);

        SteveMod.LOGGER.debug("[Ollama] Request JSON (truncated): {}",
            requestBody.toString().substring(0, Math.min(512, requestBody.toString().length())));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(120)) // Ollama can be slower on CPU
            .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .build();

        try {
            long start = System.currentTimeMillis();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            long duration = System.currentTimeMillis() - start;
            SteveMod.LOGGER.info("[Ollama] HTTP status {} in {} ms", response.statusCode(), duration);

            if (response.statusCode() == 200) {
                JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                
                // Ollama response format: { "message": { "role": "assistant", "content": "..." } }
                if (jsonResponse.has("message")) {
                    JsonObject message = jsonResponse.getAsJsonObject("message");
                    return message.get("content").getAsString();
                } else {
                    SteveMod.LOGGER.error("Ollama API response missing 'message' field");
                    SteveMod.LOGGER.error("Response: {}", response.body());
                    return null;
                }
            } else {
                SteveMod.LOGGER.error("[Ollama] API request failed: {}", response.statusCode());
                String body = response.body();
                if (body != null && !body.isEmpty()) {
                    SteveMod.LOGGER.error("[Ollama] Response body (truncated): {}",
                        body.substring(0, Math.min(512, body.length())));
                } else {
                    SteveMod.LOGGER.error("[Ollama] Empty response body");
                }
                SteveMod.LOGGER.error("Make sure Ollama is running (ollama serve) and model '{}' is installed", model);
                return null;
            }
        } catch (java.net.ConnectException e) {
            SteveMod.LOGGER.error("[Ollama] Failed to connect to {}. Make sure Ollama is running (ollama serve)", baseUrl);
            return null;
        } catch (Exception e) {
            SteveMod.LOGGER.error("[Ollama] Error sending request to Ollama API", e);
            return null;
        }
    }
}
