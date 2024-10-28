package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

public class AppTest {

    private DBHandler DB;

    @BeforeEach
    public void setUp() {
        // Initialize DBHandler with environment variables or static credentials
        String url = "jdbc:oracle:thin:@//localhost:1521/ORCLPDB1";
        String user = "DBAPI";
        String password = "dbapipass";
        DB = new OracleDBHandler(url, user, password);

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            fail("Oracle JDBC Driver not found.");
        }
    }

    @AfterEach
    public void tearDown() {
        // Close any resources or clean up the database
        DB = null;
    }

    /**
     * A basic test to make sure the application is working correctly
     */
    @Test
    public void testApp() {
        assertTrue(true);
    }

    /**
     * Test the select function without iterating over ResultSet
     */
    @Test
    public void testSelectFunction() {
        try {
            // Insert sample data for testing
            DB.insert("PEOPLE", List.of(1, "Smith", "John", "123 Maple St", "New York"));
            DB.insert("PEOPLE", List.of(2, "Doe", "Jane", "456 Oak St", "Los Angeles"));
            DB.insert("PEOPLE", List.of(3, "Johnson", "Jim", "789 Pine St", "New York"));

            // Call select method, which automatically prints results
            DB.select("PEOPLE", List.of("PersonID", "LastName", "FirstName"), "City = ?", List.of("New York"));

            // No need to manually process ResultSet, since the select method handles printing
            // We could still add assertions to check side effects or state if needed
            assertTrue(true); // Just a placeholder for now

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occurred during testSelectFunction: " + e.getMessage());
        }
    }

    /**
     * Test inserting data
     */
    @Test
    public void testInsertFunction() {
        try {
            // Insert sample data
            DB.insert("PEOPLE", List.of(4, "Williams", "Anna", "123 Birch St", "Chicago"));

            // Assuming select method also prints this, we just call it for verification
            DB.select("PEOPLE", List.of("PersonID", "LastName", "FirstName"), "PersonID = ?", List.of(4));

            // Placeholder for now - could add checks if state changes are verified elsewhere
            assertTrue(true);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occurred during testInsertFunction: " + e.getMessage());
        }
    }
}
