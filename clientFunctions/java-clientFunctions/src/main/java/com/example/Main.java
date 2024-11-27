package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    public static void main(String[] args) {
        try {
            ApiClient apiClient = new ApiClient("http://localhost:8080");
            ObjectMapper objectMapper = new ObjectMapper();

            // Example usage of execQuery
            JsonNode queryResult = apiClient.execQuery("SELECT * FROM PEOPLE");
            if (queryResult != null) {
                System.out.println("Query Result: " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(queryResult));
            }


            // Example usage of listTables
            JsonNode tables = apiClient.listTables();
            if (tables != null) {
                System.out.println(tables.path("data").asText());
            }
            // Example usage of insert
            //boolean insertResult = apiClient.insert("PEOPLE", "[10, \"Doe\", \"John\", \"Renner Rd\", \"Dallas\"]");
            //System.out.println("Insert Result: " + (insertResult ? "Success" : "Failure"));

            // Example usage of delete
            //boolean deleteResult = apiClient.delete("PEOPLE", "[\"FirstName\"]", "[\"John\"]");
            //System.out.println("Delete Result: " + (deleteResult ? "Success" : "Failure"));
            // Example usage of select
            JsonNode selectResult = apiClient.select("PEOPLE", "[\"firstname\", \"address\"]", "firstname LIKE ?", "[\"A%\"]");
            if (selectResult != null) {
                System.out.println("Select Result: " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(selectResult));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

