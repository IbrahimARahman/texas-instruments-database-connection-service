package org.example.DatabaseAPI.controller;

import org.example.OracleDBHandler;
import org.example.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseControllerTest {

    private OracleDBHandler dbHandlerStub;
    private DatabaseController databaseController;

    @BeforeEach
    void setUp() {
        // Initialize a custom OracleDBHandler stub
        dbHandlerStub = new OracleDBHandlerStub();
        databaseController = new DatabaseController(dbHandlerStub);
    }

    // Test for successful execution of a valid SQL query
    @Test
    void testExecQuery_Success() {
        ResponseEntity<Map<String, Object>> response = databaseController.execQuery(Map.of("query", "SELECT * FROM test"));
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Query executed successfully", response.getBody().get("message"));
        assertEquals("[{\"id\":1,\"name\":\"Test\"}]", response.getBody().get("data"));
    }

    // Test for handling a missing or empty query string
    @Test
    void testExecQuery_MissingQuery() {
        ResponseEntity<Map<String, Object>> response = databaseController.execQuery(Map.of());
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("Query string is missing or empty", response.getBody().get("message"));
    }

    // Test for successfully creating a table
    @Test
    void testCreateTable_Success() {
        ResponseEntity<Map<String, Object>> response = databaseController.createTable("CREATE TABLE test (id INT, name VARCHAR(50))");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Table created successfully", response.getBody().get("message"));
    }

    // Test for retrieving a list of tables
    @Test
    void testListTables_Success() {
        ResponseEntity<Map<String, Object>> response = databaseController.listTables();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Tables listed successfully", response.getBody().get("message"));
        assertEquals("[\"test_table\"]", response.getBody().get("data"));
    }

    // Test for successful data insertion into a table
    @Test
    void testInsert_Success() {
        ResponseEntity<Map<String, Object>> response = databaseController.insert(Map.of(
                "tableName", "test_table",
                "values", List.of(1, "Test")
        ));
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Data inserted successfully", response.getBody().get("message"));
    }

    // Test for retrieving data from a table
    @Test
    void testDelete_Success() {
        ResponseEntity<Map<String, Object>> response = databaseController.delete(Map.of(
                "tableName", "test_table",
                "columns", List.of("id"),
                "values", List.of(1)
        ));
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Rows deleted successfully", response.getBody().get("message"));
    }

    @Test
    void testSelect_Success() {
        ResponseEntity<Map<String, Object>> response = databaseController.select(Map.of(
                "tableName", "test_table",
                "columns", List.of("id", "name"),
                "whereClause", "id = ?",
                "params", List.of(1)
        ));
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Data retrieved successfully", response.getBody().get("message"));
        assertEquals("[{\"id\":1,\"name\":\"Test\"}]", response.getBody().get("data"));
    }

    // Custom Stub Class for OracleDBHandler
    private static class OracleDBHandlerStub extends OracleDBHandler {

        public OracleDBHandlerStub() {
            super("jdbc:h2:mem:testdb", "sa", "");
        }

        @Override
        public Result execQuery(String query) {
            if (query.contains("SELECT")) {
                return new Result("success", "Query executed successfully", "[{\"id\":1,\"name\":\"Test\"}]");
            }
            return new Result("error", "Invalid query");
        }

        @Override
        public Result createTable(String sql) {
            if (sql.contains("CREATE TABLE")) {
                return new Result("success", "Table created successfully");
            }
            return new Result("error", "Invalid SQL");
        }

        @Override
        public Result listTables() {
            return new Result("success", "Tables listed successfully", "[\"test_table\"]");
        }

        @Override
        public Result insert(String tableName, List<Object> values) {
            return new Result("success", "Data inserted successfully");
        }

        @Override
        public Result delete(String tableName, List<String> columns, List<Object> values) {
            return new Result("success", "Rows deleted successfully");
        }

        @Override
        public Result select(String tableName, List<String> columns, String whereClause, List<Object> params) {
            return new Result("success", "Data retrieved successfully", "[{\"id\":1,\"name\":\"Test\"}]");
        }
    }
}
