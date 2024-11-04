import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class ApiClient {

    private final String baseUrl;
    private final HttpClient httpClient;

    // Constructor: Initialize the base URL for the API and the HttpClient
    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
    }

    // Function to execute a query by sending a POST request to the API
    public String execQuery(String query) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/execQuery"))
                    .header("Content-Type", "text/plain")
                    .POST(HttpRequest.BodyPublishers.ofString(query, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.println("HTTP POST request failed with response code: " + response.statusCode());
                return "";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "";
        }
    }

    // Function to perform a GET request to /listTables
    public String listTables() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/listTables"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.println("HTTP GET request failed with response code: " + response.statusCode());
                return "";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "";
        }
    }

        // Function to perform an INSERT operation
    public String insert(String tableName, List<Object> values) {
        String jsonData = "{\"tableName\": \"" + tableName + "\", \"values\": " + values.toString() + "}";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/insert"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonData, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.println("HTTP POST request failed with response code: " + response.statusCode());
                return "";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "";
        }
    }

    // Function to perform a DELETE operation
    public String delete(String tableName, List<String> columns, List<Object> values) {
        String jsonData = "{\"tableName\": \"" + tableName + "\", \"columns\": " + columns.toString() + ", \"values\": " + values.toString() + "}";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/delete"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonData, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.println("HTTP POST request failed with response code: " + response.statusCode());
                return "";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "";
        }
    }

    // Function to perform a SELECT operation
    public String select(String tableName, List<String> columns, String whereClause, List<Object> params) {
        String jsonData = "{\"tableName\": \"" + tableName + "\", \"columns\": " + columns.toString() + ", \"whereClause\": \"" + whereClause + "\", \"params\": " + params.toString() + "}";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/select"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonData, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.println("HTTP POST request failed with response code: " + response.statusCode());
                return "";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args) {
        // Example usage of the ApiClient
        ApiClient apiClient = new ApiClient("http://localhost:8080");

        // Execute a query using POST
        String query = "SELECT * FROM PEOPLE";
        String queryResponse = apiClient.execQuery(query);
        System.out.println(queryResponse);

        // Get the list of tables using GET
        String tablesResponse = apiClient.listTables();
        System.out.println(tablesResponse);
    }
}

