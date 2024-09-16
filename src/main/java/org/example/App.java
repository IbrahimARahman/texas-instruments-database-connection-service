package org.example;

import io.github.cdimascio.dotenv.Dotenv;

import javax.xml.transform.Result;
import java.sql.ResultSet;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        Dotenv dotenv = Dotenv.load();
        DBHandler DB = new OracleDBHandler(dotenv.get("DB_URL"), dotenv.get("DB_USER"), dotenv.get("DB_PASSWORD"));
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            DB.execQuery("CREATE TABLE People (\n" +
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
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }
}
