package org.example;

import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DBHandler {
    private final String url;
    private final String username;
    private final String password;

    public DBHandler(String url, String user, String pass) {
        this.url = url;
        this.username = user;
        this.password = pass;
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public Result execQuery(String query) {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            // Check if the query is a SELECT
            if (query.trim().toUpperCase().startsWith("SELECT")) {
                ResultSet rs = stmt.executeQuery(query);
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Collect query results as a list of maps
                List<Map<String, Object>> rows = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    rows.add(row);
                }

                // Convert results to JSON
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResult = objectMapper.writeValueAsString(rows);

                // Return success with JSON result
                if (rows.isEmpty()) {
                    return new Result("success", "Query executed successfully, but no results were found.");
                }
                return new Result("success", "Query executed successfully", jsonResult);

            } else {
                // Handle non-SELECT queries
                int rowsAffected = stmt.executeUpdate(query);
                return new Result("success", "Query executed successfully. Rows affected: " + rowsAffected);
            }
        } catch (SQLException e) {
            return new Result("error", "SQL Error: " + e.getMessage());
        } catch (Exception e) {
            return new Result("error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    public Result createTable(String sqlStr) {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlStr);
            return new Result("success", "Table created successfully.");
        } catch (SQLException e) {
            return new Result("error", "SQL Error: " + e.getMessage());
        }
    }

    public abstract Result listTables();

    abstract String toJavaLikeType(String dataType, int dataLength);

    abstract String generateExampleValue(String dataType, int dataLength);

    public abstract Result insert(String tableName, List<Object> values);

    abstract boolean isValueCompatibleWithType(Object value, String expectedDataType);

    public abstract Result delete(String tableName, List<String> columns, List<Object> values);

    public Result select(String tableName, List<String> columns, String whereClause, List<Object> params) {
        StringBuilder query = new StringBuilder("SELECT ");

        // Append column names or "*" if no columns are specified
        if (columns == null || columns.isEmpty()) {
            query.append("*");
        } else {
            query.append(String.join(", ", columns));
        }
        query.append(" FROM ").append(tableName);

        // Append WHERE clause if provided
        if (whereClause != null && !whereClause.isEmpty()) {
            query.append(" WHERE ").append(whereClause);
        }

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            // Set the parameters for the WHERE clause
            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }
            }

            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Collect query results as a list of maps
            List<Map<String, Object>> rows = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                rows.add(row);
            }

            // Convert results to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResult = objectMapper.writeValueAsString(rows);

            // Return success with JSON result
            if (rows.isEmpty()) {
                return new Result("success", "Query executed successfully, but no results were found.");
            }
            return new Result("success", "Query executed successfully", jsonResult);

        } catch (SQLException e) {
            return new Result("error", "SQL Error: " + e.getMessage());
        } catch (Exception e) {
            return new Result("error", "An unexpected error occurred: " + e.getMessage());
        }
    }
}