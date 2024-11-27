package com.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    private JsonNode sendRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return objectMapper.readTree(response.body());
            } else {
                System.err.println("HTTP request failed with code: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error during HTTP request: " + e.getMessage());
            return null;
        }
    }

    public JsonNode execQuery(String query) {
        String jsonData = String.format("{\"query\": \"%s\"}", query);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/execQuery"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                .build();
        return sendRequest(request);
    }

    public boolean createTable(String tableSql) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/createTable"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(tableSql))
                .build();
        JsonNode response = sendRequest(request);
        return response != null && "success".equals(response.path("status").asText());
    }

    public JsonNode listTables() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/listTables"))
                .GET()
                .build();
        return sendRequest(request);
    }

    public boolean insert(String tableName, String values) {
        try {
            // Validate that the input "values" is a JSON object
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.readTree(values); // Ensures it's valid JSON

            // Create the JSON payload for the HTTP request
            String jsonData = String.format("{\"tableName\": \"%s\", \"values\": %s}", tableName, values);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/insert"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                    .build();

            // Send the request and process the response
            JsonNode response = sendRequest(request);
            return response != null && "success".equals(response.path("status").asText());
        } catch (Exception e) {
            System.err.println("Error during insert: " + e.getMessage());
            return false;
        }
    }


    public boolean deleteData(String tableName, String columns, String values) {
        String jsonData = String.format("{\"tableName\": \"%s\", \"columns\": %s, \"values\": %s}", tableName, columns, values);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/delete"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                .build();
        JsonNode response = sendRequest(request);
        return response != null && "success".equals(response.path("status").asText());
    }

    public JsonNode select(String tableName, String columns, String whereClause, String params) {
        String jsonData = String.format(
            "{\"tableName\": \"%s\", \"columns\": %s, \"whereClause\": \"%s\", \"params\": %s}",
            tableName, columns, whereClause, params
        );
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/select"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                .build();
        return sendRequest(request);
    }
}
