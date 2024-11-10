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

    // Constructor: Initialize the base URL for the API and the HttpClient
    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // Helper function to send requests and parse responses as JSON
    private JsonNode sendRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return objectMapper.readTree(response.body());
            } else {
                throw new RuntimeException("HTTP request failed with code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error during HTTP request: " + e.getMessage(), e);
        }
    }

    // Function to execute a query by sending a POST request to the API
    public JsonNode execQuery(String query) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/execQuery"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(query))
                .build();
        return sendRequest(request);
    }

    // Function to perform a GET request to /listTables
    public JsonNode listTables() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/listTables"))
                .GET()
                .build();
        return sendRequest(request);
    }

        // Function to perform an INSERT operation
    public JsonNode insert(String tableName, String values) {
        String jsonData = String.format("{\"tableName\": \"%s\", \"values\": %s}", tableName, values);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/insert"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                .build();
        return sendRequest(request);
    }

    // Function to perform a DELETE operation
    public JsonNode delete(String tableName, String columns, String values) {
        String jsonData = String.format("{\"tableName\": \"%s\", \"columns\": %s, \"values\": %s}", tableName, columns, values);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/delete"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                .build();
        return sendRequest(request);
    }

    // Function to perform a SELECT operation
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
    
    public static void main(String[] args) {
        try {
            ApiClient apiClient = new ApiClient("http://localhost:8080");
            
            // Example usage of execQuery
            JsonNode queryResult = apiClient.execQuery("{\"query\": \"SELECT * FROM PEOPLE\"}");
            System.out.println("Query Result: " + queryResult.toPrettyString());
            
            // Example usage of listTables
            JsonNode tables = apiClient.listTables();
            System.out.println("Tables: " + tables.toPrettyString());

            // Example usage of insert
            JsonNode insertResult = apiClient.insert("PEOPLE", "[{\"name\":\"John Doe\",\"age\":30}]");
            System.out.println("Insert Result: " + insertResult.toPrettyString());

            // Example usage of delete
            JsonNode deleteResult = apiClient.delete("PEOPLE", "[\"name\"]", "[\"John Doe\"]");
            System.out.println("Delete Result: " + deleteResult.toPrettyString());

            // Example usage of select
            JsonNode selectResult = apiClient.select("PEOPLE", "[\"name\", \"age\"]", "age > ?", "[25]");
            System.out.println("Select Result: " + selectResult.toPrettyString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
