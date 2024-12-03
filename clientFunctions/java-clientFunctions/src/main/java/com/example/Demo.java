package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Demo {
    public static void main(String[] args) {
        try {
            ApiClient apiClient = new ApiClient("http://localhost:8080");
            ObjectMapper objectMapper = new ObjectMapper();

            /*boolean success = apiClient.createTable("CREATE TABLE people (\n" +
                    "    id INT PRIMARY KEY,\n" +
                    "    lastname VARCHAR(255) NOT NULL,\n" +
                    "    firstname VARCHAR(255) NOT NULL,\n" +
                    "    street VARCHAR(255),\n" +
                    "    city VARCHAR(255)\n" +
                    ")");
            System.out.println(success);*/

            /*JsonNode tables = apiClient.listTables();
            if (tables != null) {
                System.out.println(tables.path("data").asText());
            }*/

            // Example usage of insert
            /*boolean insert1 = apiClient.insert("PEOPLE", "[1, \"Doe\", \"John\", \"Renner Rd\", \"Dallas\"]");
            boolean insert2 = apiClient.insert("PEOPLE", "[2, \"Doe\", \"Jane\", \"Main Rd\", \"Houston\"]");
            boolean insert3 = apiClient.insert("PEOPLE", "[3, \"Abdel Rahman\", \"Ibrahim\", \"Belt Line Rd\", \"El Paso\"]");
            System.out.println(insert1 + " " + insert2 + " " + insert3);*/

            // Example usage of execQuery
            /*JsonNode queryResult = apiClient.execQuery("SELECT * FROM PEOPLE");
            if (queryResult != null) {
                System.out.println("Query Result: " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(queryResult));
            }*/

            // Example usage of delete
            boolean deleteResult = apiClient.deleteData("PEOPLE", "[\"firstName\"]", "[\"Jane\"]");
            System.out.println("Delete Result: " + (deleteResult ? "Success" : "Failure"));
            // Example usage of select
            /*JsonNode selectResult = apiClient.select("PEOPLE", "[\"firstname\", \"street\"]", "lastname = ?", "[\"Doe\"]");
            if (selectResult != null) {
                System.out.println("Select Result: " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(selectResult));
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

