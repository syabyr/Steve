package com.steve.ai.ai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OllamaSmokeTest {
    public static void main(String[] args) throws Exception {
        String apiUrl = "http://202.1.1.121:11434/api/chat";

        String body = """
            {
              "model": "llama3.1:8b",
              "stream": false,
              "messages": [
                { "role": "user", "content": "help" }
              ]
            }
            """;

        HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Content-Type", "application/json")
            .header("Accept", "*/*")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status: " + response.statusCode());
        System.out.println("Body: " + response.body());
    }
}