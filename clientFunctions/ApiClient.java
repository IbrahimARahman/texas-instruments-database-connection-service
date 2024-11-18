import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {

    private final String baseUrl;
    private final HttpClient httpClient;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
    }

    private JSONObject sendRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return new JSONObject(response.body());
            } else {
                System.err.println("HTTP request failed with code: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error during HTTP request: " + e.getMessage());
            return null;
        }
    }

    public JSONObject execQuery(String query) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/execQuery"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"query\": \"" + query + "\"}"))
                .build();
        return sendRequest(request);
    }

    public boolean createTable(String tableSql) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/createTable"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(tableSql))
                .build();
        JSONObject response = sendRequest(request);
        return response != null && "success".equals(response.optString("status"));
    }

    public JSONObject listTables() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/listTables"))
                .GET()
                .build();
        return sendRequest(request);
    }

    public boolean insert(String tableName, String values) {
        String jsonData = String.format("{\"tableName\": \"%s\", \"values\": %s}", tableName, values);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/insert"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                .build();
        JSONObject response = sendRequest(request);
        return response != null && "success".equals(response.optString("status"));
    }

    public boolean delete(String tableName, String columns, String values) {
        String jsonData = String.format("{\"tableName\": \"%s\", \"columns\": %s, \"values\": %s}", tableName, columns, values);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/delete"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                .build();
        JSONObject response = sendRequest(request);
        return response != null && "success".equals(response.optString("status"));
    }

    public JSONObject select(String tableName, String columns, String whereClause, String params) {
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

