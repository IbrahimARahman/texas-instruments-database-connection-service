package org.example;

import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.List;

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
                StringBuilder resultData = new StringBuilder();

                // Iterate through the ResultSet to build the output string
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object columnValue = rs.getObject(i);
                        resultData.append(columnName).append(": ").append(columnValue).append(", ");
                    }
                    // Remove the trailing comma and space
                    if (resultData.length() > 0) {
                        resultData.setLength(resultData.length() - 2);
                    }
                    resultData.append("\n"); // Add a newline after each row
                }

                // Return success result with data if rows are retrieved
                if (resultData.length() == 0) {
                    return new Result("success", "Query executed successfully, but no results were found.");
                }
                return new Result("success", "Query executed successfully", resultData.toString());

            } else {
                // Handle non-SELECT queries
                int rowsAffected = stmt.executeUpdate(query);
                return new Result("success", "Query executed successfully. Rows affected: " + rowsAffected);
            }
        } catch (SQLException e) {
            return new Result("error", "SQL Error: " + e.getMessage());
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

    public abstract Result select(String tableName, List<String> columns, String whereClause, List<Object> params);
}
