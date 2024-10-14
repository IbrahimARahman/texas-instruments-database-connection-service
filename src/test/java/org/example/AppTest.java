/*package org.example;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     *
    public void testApp()
    {
        assertTrue( true );
    }
}*/
package org.example;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.ResultSet;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

    private DBHandler DB;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
        // Initialize DBHandler
        Dotenv dotenv = Dotenv.load();
        DB = new OracleDBHandler(dotenv.get("DB_URL"), dotenv.get("DB_USER"), dotenv.get("DB_PASSWORD"));
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigorous Test :-)
     */
    public void testApp() {
        assertTrue(true);
    }

    /**
     * Test the select function
     */
    public void testSelectFunction() {
        try {
            // Insert sample data for testing
            DB.insert("PEOPLE", List.of(1, "Smith", "John", "123 Maple St", "New York"));
            DB.insert("PEOPLE", List.of(2, "Doe", "Jane", "456 Oak St", "Los Angeles"));
            DB.insert("PEOPLE", List.of(3, "Johnson", "Jim", "789 Pine St", "New York"));

            // Perform select query
            ResultSet rs = DB.select("PEOPLE", List.of("PersonID", "LastName", "FirstName"), "City = ?", List.of("New York"));

            // Check that the correct number of rows are returned
            int rowCount = 0;
            while (rs != null && rs.next()) {
                rowCount++;
                int personID = rs.getInt("PersonID");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                System.out.println("PersonID: " + personID + ", Name: " + firstName + " " + lastName);

                // Add assertions to verify the data
                assertTrue(personID == 1 || personID == 3);
            }
            assertEquals(2, rowCount);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occurred during testSelectFunction: " + e.getMessage());
        }
    }
}


