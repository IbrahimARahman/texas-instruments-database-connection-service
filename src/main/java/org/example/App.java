package org.example;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class App {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        DBHandler DB = new OracleDBHandler(dotenv.get("DB_URL"), dotenv.get("DB_USER"), dotenv.get("DB_PASSWORD"));
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            /*DB.execQuery("CREATE TABLE People (\n" +
                        "    PersonID int PRIMARY KEY,\n" +
                        "    LastName varchar(255),\n" +
                        "    FirstName varchar(255),\n" +
                        "    Address varchar(255),\n" +
                        "    City varchar(255)\n" +
                        ")");*/
            //DB.listTables();
            //DB.insert("PEOPLE", List.of(1, "Smith", "John", "123 Maple St", "New York"));
            //DB.insert("PEOPLE", List.of(2, "Doe", "Jane", "456 Oak St", "Los Angeles"));
            //DB.insert("PEOPLE", List.of(3, "Johnson", "Jim", "789 Pine St", "New York"));

            // Example of using the select method
            // Select people from New York
            DB.select("PEOPLE", List.of("PersonID", "LastName", "FirstName"), "City = ?", List.of("New York"));
            // Example with AND condition
            // Select people from New York with LastName 'Smith'
            DB.select("PEOPLE", List.of("PersonID", "LastName", "FirstName"), "City = ? AND LastName = ?", List.of("New York", "Smith"));
            // Example with IN condition
            // Select people from cities in a list
            DB.select("PEOPLE", List.of("PersonID", "LastName", "FirstName"), "City IN (?, ?)", List.of("New York", "Los Angeles"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}