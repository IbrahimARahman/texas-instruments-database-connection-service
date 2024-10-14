/*package org.example;

import io.github.cdimascio.dotenv.Dotenv;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.util.List;

public class App {
    public static void main( String[] args ) {
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
                        ")");
            DB.execQuery("CREATE TABLE Pets (\n" +
                        "    PetID int PRIMARY KEY,\n" +
                        "    Name varchar(255),\n" +
                        "    AnimalType varchar(255),\n" +
                        "    Breed varchar(255)\n" +
                        ")");
            DB.listTables();
            //DB.insert("PEOPLE", List.of(1, "Smith", "John", "123 Maple St", "New York"));
            //DB.insert("PEOPLE", List.of("hello", 2, "John", "123 Maple St", "New York"));
            //DB.insert("PEOPLE", List.of(1, "John", "123 Maple St", "New York"));
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }
}*/
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
                        ")");
            DB.execQuery("CREATE TABLE Pets (\n" +
                        "    PetID int PRIMARY KEY,\n" +
                        "    Name varchar(255),\n" +
                        "    AnimalType varchar(255),\n" +
                        "    Breed varchar(255)\n" +
                        ")");*/
            DB.listTables();
            //DB.insert("PEOPLE", List.of(1, "Smith", "John", "123 Maple St", "New York"));
            //DB.insert("PEOPLE", List.of(2, "Doe", "Jane", "456 Oak St", "Los Angeles"));
            //DB.insert("PEOPLE", List.of(3, "Johnson", "Jim", "789 Pine St", "New York"));

            // Example of using the select method
            // Select people from New York
            ResultSet rs = DB.select("PEOPLE", List.of("PersonID", "LastName", "FirstName"), "City = ?", List.of("New York"));
            while (rs != null && rs.next()) {
                System.out.println("PersonID: " + rs.getInt("PersonID") + ", Name: " + rs.getString("FirstName") + " " + rs.getString("LastName"));
            }

            // Example with AND condition
            // Select people from New York with LastName 'Smith'
            rs = DB.select("PEOPLE", List.of("PersonID", "LastName", "FirstName"), "City = ? AND LastName = ?", List.of("New York", "Smith"));
            while (rs != null && rs.next()) {
                System.out.println("PersonID: " + rs.getInt("PersonID") + ", Name: " + rs.getString("FirstName") + " " + rs.getString("LastName"));
            }

            // Example with IN condition
            // Select people from cities in a list
            rs = DB.select("PEOPLE", List.of("PersonID", "LastName", "FirstName"), "City IN (?, ?)", List.of("New York", "Los Angeles"));
            while (rs != null && rs.next()) {
                System.out.println("PersonID: " + rs.getInt("PersonID") + ", Name: " + rs.getString("FirstName") + " " + rs.getString("LastName"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}