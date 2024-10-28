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

    public String execQuery(String query) {
        try (Connection conn = connect()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            StringBuilder result = new StringBuilder();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = rs.getObject(i);
                    result.append(columnName).append(": ").append(columnValue).append(", ");
                }
                // Remove the trailing comma and space
                if (result.length() > 0) {
                    result.setLength(result.length() - 2);
                }
                result.append("\n"); // Add a newline after each row
            }
            System.out.println(result.toString());
            return result.toString();
        } catch (Exception e) {
            return e.toString();
        }
    }

    /*public int createTable(String sqlStr) {

    }*/

    public abstract String listTables();

    abstract String toJavaLikeType(String dataType, int dataLength);

    abstract String generateExampleValue(String dataType, int dataLength);

    public abstract void insert(String tableName, List<Object> values);

    abstract boolean isValueCompatibleWithType(Object value, String expectedDataType);

    public abstract void delete(String tableName, List<String> columns, List<Object> values);

    // New abstract select method
    public abstract void select(String tableName, List<String> columns, String whereClause, List<Object> params);
}
