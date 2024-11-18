import org.json.JSONObject;

public class Main {
    public static void main(String[] args) {
        try {
            ApiClient apiClient = new ApiClient("http://localhost:8080");

            // Example usage of execQuery
            JSONObject queryResult = apiClient.execQuery("SELECT * FROM PEOPLE");
            if (queryResult != null) {
                System.out.println("Query Result: " + queryResult.toString(4)); // Pretty print JSON
            }

            // Example usage of listTables
            JSONObject tables = apiClient.listTables();
            if (tables != null) {
                System.out.println("Tables: " + tables.toString(4)); // Pretty print JSON
            }

            // Example usage of insert
            boolean insertResult = apiClient.insert("PEOPLE", "[{\"name\":\"John Doe\",\"age\":30}]");
            System.out.println("Insert Result: " + (insertResult ? "Success" : "Failure"));

            // Example usage of delete
            boolean deleteResult = apiClient.delete("PEOPLE", "[\"name\"]", "[\"John Doe\"]");
            System.out.println("Delete Result: " + (deleteResult ? "Success" : "Failure"));

            // Example usage of select
            JSONObject selectResult = apiClient.select("PEOPLE", "[\"name\", \"age\"]", "age > ?", "[25]");
            if (selectResult != null) {
                System.out.println("Select Result: " + selectResult.toString(4)); // Pretty print JSON
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

