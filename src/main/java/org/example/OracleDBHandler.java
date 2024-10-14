/*package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class OracleDBHandler extends DBHandler {
    public OracleDBHandler(String url, String user, String password) {
        super(url, user, password);
    }

    public void listTables() {
        String query = "SELECT table_name, column_name, data_type, data_length " +
                "FROM user_tab_columns " +
                "ORDER BY table_name, column_id";
        try (Connection conn = connect()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            String currentTable = "";
            StringBuilder insertStatement = new StringBuilder();
            StringBuilder exampleValues = new StringBuilder();

            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                String columnName = rs.getString("COLUMN_NAME");
                String dataType = rs.getString("DATA_TYPE");
                int dataLength = rs.getInt("DATA_LENGTH");

                // Print table name only once when it changes
                if (!tableName.equals(currentTable)) {
                    if (!currentTable.isEmpty()) {
                        // Print example insert statement for the previous table
                        System.out.println("Example Insert: insert(\"" + currentTable + "\", " + exampleValues.append("));").toString());
                    }
                    currentTable = tableName;
                    insertStatement.setLength(0); // Reset for the new table
                    exampleValues.setLength(0);
                    exampleValues.append("List.of(");
                    System.out.println("\nTable: " + tableName);
                }

                // Append values for the example insert
                if (exampleValues.charAt(exampleValues.length() - 1) != '(') {
                    exampleValues.append(", ");
                }
                exampleValues.append(generateExampleValue(dataType, dataLength));

                // Print column details with Java/C++-like types
                System.out.println(columnName + " (" + toJavaLikeType(dataType, dataLength) + ")");
            }

            // Print example insert statement for the last table
            if (!currentTable.isEmpty()) {
                System.out.println("Example Insert: insert(\"" + currentTable + "\", " + exampleValues.append("));").toString());
            }
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper to convert SQL data types to Java/C++-like data types
    String toJavaLikeType(String dataType, int dataLength) {
        switch (dataType.toUpperCase()) {
            case "VARCHAR2":
            case "CHAR":
                return "String";
            case "NUMBER":
                if (dataLength == 0) {
                    return "double";  // Assume floating point for general numbers
                }
                return dataLength <= 10 ? "int" : "long";  // Assuming int for small numbers, long for larger
            case "DATE":
                return "LocalDate";  // For Java-like date type
            default:
                return "Object";  // Fallback
        }
    }

    // Helper to generate example values based on data type
    String generateExampleValue(String dataType, int dataLength) {
        switch (dataType.toUpperCase()) {
            case "VARCHAR2":
            case "CHAR":
                return "\"example_string\"";
            case "NUMBER":
                return dataLength <= 10 ? "123" : "1234567890";  // Use integer for smaller numbers, long for larger
            case "DATE":
                return "LocalDate.now()";  // Example date
            default:
                return "null";  // Fallback
        }
    }


    public void insert(String tableName, List<Object> values) {
        String checkTableQuery = "SELECT COUNT(*) FROM user_tables WHERE table_name = UPPER(?)";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkTableQuery)) {

            // Checking if the table exists
            checkStmt.setString(1, tableName);
            ResultSet tableExistsResult = checkStmt.executeQuery();
            if (tableExistsResult.next() && tableExistsResult.getInt(1) == 0) {
                System.out.println("Table " + tableName + " does not exist.");
                return;
            }

            // Getting the schema info
            String getColumnsQuery = "SELECT column_name, data_type FROM user_tab_columns WHERE table_name = UPPER(?) ORDER BY column_id";
            try (PreparedStatement columnsStmt = conn.prepareStatement(getColumnsQuery)) {
                columnsStmt.setString(1, tableName);
                ResultSet columnsResult = columnsStmt.executeQuery();

                StringBuilder columns = new StringBuilder();
                StringBuilder placeholders = new StringBuilder();
                int columnCount = 0;
                List<String> dataTypes = new ArrayList<>();

                while (columnsResult.next()) {
                    if (columnCount > 0) {
                        columns.append(", ");
                        placeholders.append(", ");
                    }
                    columns.append(columnsResult.getString("COLUMN_NAME"));
                    placeholders.append("?");
                    dataTypes.add(columnsResult.getString("DATA_TYPE"));
                    columnCount++;
                }

                // Verifying schema and list alignment
                if (columnCount != values.size()) {
                    System.out.println("Error: Number of values (" + values.size() + ") does not match the number of columns (" + columnCount + ").");
                    return;
                }

                // Checking data types
                for (int i = 0; i < values.size(); i++) {
                    Object value = values.get(i);
                    String expectedDataType = dataTypes.get(i).toUpperCase();
                    if (!isValueCompatibleWithType(value, expectedDataType)) {
                        System.out.println("Error: Value '" + value + "' at index " + (i + 1) + " does not match expected data type '" + expectedDataType + "'.");
                        return;
                    }
                }

                // Inserting values
                String insertQuery = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    for (int i = 0; i < values.size(); i++) {
                        insertStmt.setObject(i + 1, values.get(i));
                    }
                    insertStmt.executeUpdate();
                    System.out.println("Values inserted successfully into " + tableName);
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    // Helper function to check if a value is compatible with the expected SQL data type
    boolean isValueCompatibleWithType(Object value, String expectedDataType) {
        if (value == null) {
            return true;  // Null can be inserted into any column
        }

        switch (expectedDataType) {
            case "VARCHAR2":
            case "CHAR":
                return value instanceof String;
            case "NUMBER":
                return value instanceof Number;
            case "DATE":
                return value instanceof java.sql.Date || value instanceof java.time.LocalDate;
            // Add other types as needed
            default:
                return true;  // Assuming compatibility for unsupported types
        }
    }

    public void delete(String tableName, List<String> columns, List<Object> values) {
        String checkTableQuery = "SELECT COUNT(*) FROM user_tables WHERE table_name = UPPER(?)";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkTableQuery)) {
            // Check if the table exists
            checkStmt.setString(1, tableName);
            ResultSet tableExistsResult = checkStmt.executeQuery();
            if (tableExistsResult.next() && tableExistsResult.getInt(1) == 0) {
                System.out.println("Table " + tableName + " does not exist.");
                return;
            }

            if (columns.size() != values.size()) {
                System.out.println("Number of columns does not match number of values.");
                return;
            }

            // Get the schema information
            String getColumnsQuery = "SELECT column_name FROM user_tab_columns WHERE table_name = UPPER(?)";
            try (PreparedStatement columnsStmt = conn.prepareStatement(getColumnsQuery)) {
                columnsStmt.setString(1, tableName);
                ResultSet columnsResult = columnsStmt.executeQuery();

                Set<String> validColumns = new HashSet<>();
                while (columnsResult.next()) {
                    validColumns.add(columnsResult.getString("COLUMN_NAME").toUpperCase());
                }

                // Verify that the columns exist in the table
                for (String column : columns) {
                    if (!validColumns.contains(column.toUpperCase())) {
                        System.out.println("Column " + column + " does not exist in table " + tableName);
                        return;
                    }
                }

                // Build the WHERE clause
                StringBuilder whereClause = new StringBuilder();
                for (int i = 0; i < columns.size(); i++) {
                    if (i > 0) {
                        whereClause.append(" AND ");
                    }
                    whereClause.append(columns.get(i)).append(" = ?");
                }

                String deleteQuery = "DELETE FROM " + tableName + " WHERE " + whereClause.toString();
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    for (int i = 0; i < values.size(); i++) {
                        deleteStmt.setObject(i + 1, values.get(i));
                    }
                    int rowsDeleted = deleteStmt.executeUpdate();
                    System.out.println(rowsDeleted + " rows deleted from " + tableName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}*/
package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class OracleDBHandler extends DBHandler {
    public OracleDBHandler(String url, String user, String password) {
        super(url, user, password);
    }

    public void listTables() {
        String query = "SELECT table_name, column_name, data_type, data_length " +
                "FROM user_tab_columns " +
                "ORDER BY table_name, column_id";
        try (Connection conn = connect()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            String currentTable = "";
            StringBuilder insertStatement = new StringBuilder();
            StringBuilder exampleValues = new StringBuilder();

            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                String columnName = rs.getString("COLUMN_NAME");
                String dataType = rs.getString("DATA_TYPE");
                int dataLength = rs.getInt("DATA_LENGTH");

                // Print table name only once when it changes
                if (!tableName.equals(currentTable)) {
                    if (!currentTable.isEmpty()) {
                        // Print example insert statement for the previous table
                        System.out.println("Example Insert: insert(\"" + currentTable + "\", " + exampleValues.append("));").toString());
                    }
                    currentTable = tableName;
                    insertStatement.setLength(0); // Reset for the new table
                    exampleValues.setLength(0);
                    exampleValues.append("List.of(");
                    System.out.println("\nTable: " + tableName);
                }

                // Append values for the example insert
                if (exampleValues.charAt(exampleValues.length() - 1) != '(') {
                    exampleValues.append(", ");
                }
                exampleValues.append(generateExampleValue(dataType, dataLength));

                // Print column details with Java/C++-like types
                System.out.println(columnName + " (" + toJavaLikeType(dataType, dataLength) + ")");
            }

            // Print example insert statement for the last table
            if (!currentTable.isEmpty()) {
                System.out.println("Example Insert: insert(\"" + currentTable + "\", " + exampleValues.append("));").toString());
            }
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper to convert SQL data types to Java/C++-like data types
    String toJavaLikeType(String dataType, int dataLength) {
        switch (dataType.toUpperCase()) {
            case "VARCHAR2":
            case "CHAR":
                return "String";
            case "NUMBER":
                if (dataLength == 0) {
                    return "double";  // Assume floating point for general numbers
                }
                return dataLength <= 10 ? "int" : "long";  // Assuming int for small numbers, long for larger
            case "DATE":
                return "LocalDate";  // For Java-like date type
            default:
                return "Object";  // Fallback
        }
    }

    // Helper to generate example values based on data type
    String generateExampleValue(String dataType, int dataLength) {
        switch (dataType.toUpperCase()) {
            case "VARCHAR2":
            case "CHAR":
                return "\"example_string\"";
            case "NUMBER":
                return dataLength <= 10 ? "123" : "1234567890";  // Use integer for smaller numbers, long for larger
            case "DATE":
                return "LocalDate.now()";  // Example date
            default:
                return "null";  // Fallback
        }
    }

    public void insert(String tableName, List<Object> values) {
        String checkTableQuery = "SELECT COUNT(*) FROM user_tables WHERE table_name = UPPER(?)";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkTableQuery)) {

            // Checking if the table exists
            checkStmt.setString(1, tableName);
            ResultSet tableExistsResult = checkStmt.executeQuery();
            if (tableExistsResult.next() && tableExistsResult.getInt(1) == 0) {
                System.out.println("Table " + tableName + " does not exist.");
                return;
            }

            // Getting the schema info
            String getColumnsQuery = "SELECT column_name, data_type FROM user_tab_columns WHERE table_name = UPPER(?) ORDER BY column_id";
            try (PreparedStatement columnsStmt = conn.prepareStatement(getColumnsQuery)) {
                columnsStmt.setString(1, tableName);
                ResultSet columnsResult = columnsStmt.executeQuery();

                StringBuilder columns = new StringBuilder();
                StringBuilder placeholders = new StringBuilder();
                int columnCount = 0;
                List<String> dataTypes = new ArrayList<>();

                while (columnsResult.next()) {
                    if (columnCount > 0) {
                        columns.append(", ");
                        placeholders.append(", ");
                    }
                    columns.append(columnsResult.getString("COLUMN_NAME"));
                    placeholders.append("?");
                    dataTypes.add(columnsResult.getString("DATA_TYPE"));
                    columnCount++;
                }

                // Verifying schema and list alignment
                if (columnCount != values.size()) {
                    System.out.println("Error: Number of values (" + values.size() + ") does not match the number of columns (" + columnCount + ").");
                    return;
                }

                // Checking data types
                for (int i = 0; i < values.size(); i++) {
                    Object value = values.get(i);
                    String expectedDataType = dataTypes.get(i).toUpperCase();
                    if (!isValueCompatibleWithType(value, expectedDataType)) {
                        System.out.println("Error: Value '" + value + "' at index " + (i + 1) + " does not match expected data type '" + expectedDataType + "'.");
                        return;
                    }
                }

                // Inserting values
                String insertQuery = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    for (int i = 0; i < values.size(); i++) {
                        insertStmt.setObject(i + 1, values.get(i));
                    }
                    insertStmt.executeUpdate();
                    System.out.println("Values inserted successfully into " + tableName);
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    // Helper function to check if a value is compatible with the expected SQL data type
    boolean isValueCompatibleWithType(Object value, String expectedDataType) {
        if (value == null) {
            return true;  // Null can be inserted into any column
        }

        switch (expectedDataType) {
            case "VARCHAR2":
            case "CHAR":
                return value instanceof String;
            case "NUMBER":
                return value instanceof Number;
            case "DATE":
                return value instanceof java.sql.Date || value instanceof java.time.LocalDate;
            // Add other types as needed
            default:
                return true;  // Assuming compatibility for unsupported types
        }
    }

    public void delete(String tableName, List<String> columns, List<Object> values) {
        String checkTableQuery = "SELECT COUNT(*) FROM user_tables WHERE table_name = UPPER(?)";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkTableQuery)) {
            // Check if the table exists
            checkStmt.setString(1, tableName);
            ResultSet tableExistsResult = checkStmt.executeQuery();
            if (tableExistsResult.next() && tableExistsResult.getInt(1) == 0) {
                System.out.println("Table " + tableName + " does not exist.");
                return;
            }

            if (columns.size() != values.size()) {
                System.out.println("Number of columns does not match number of values.");
                return;
            }

            // Get the schema information
            String getColumnsQuery = "SELECT column_name FROM user_tab_columns WHERE table_name = UPPER(?)";
            try (PreparedStatement columnsStmt = conn.prepareStatement(getColumnsQuery)) {
                columnsStmt.setString(1, tableName);
                ResultSet columnsResult = columnsStmt.executeQuery();

                Set<String> validColumns = new HashSet<>();
                while (columnsResult.next()) {
                    validColumns.add(columnsResult.getString("COLUMN_NAME").toUpperCase());
                }

                // Verify that the columns exist in the table
                for (String column : columns) {
                    if (!validColumns.contains(column.toUpperCase())) {
                        System.out.println("Column " + column + " does not exist in table " + tableName);
                        return;
                    }
                }

                // Build the WHERE clause
                StringBuilder whereClause = new StringBuilder();
                for (int i = 0; i < columns.size(); i++) {
                    if (i > 0) {
                        whereClause.append(" AND ");
                    }
                    whereClause.append(columns.get(i)).append(" = ?");
                }

                String deleteQuery = "DELETE FROM " + tableName + " WHERE " + whereClause.toString();
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    for (int i = 0; i < values.size(); i++) {
                        deleteStmt.setObject(i + 1, values.get(i));
                    }
                    int rowsDeleted = deleteStmt.executeUpdate();
                    System.out.println(rowsDeleted + " rows deleted from " + tableName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // New Select method implementation
    @Override
    public ResultSet select(String tableName, List<String> columns, String whereClause, List<Object> params) {
        try {
            Connection conn = connect();
            StringBuilder query = new StringBuilder("SELECT ");

            if (columns == null || columns.isEmpty()) {
                query.append("*");
            } else {
                query.append(String.join(", ", columns));
            }

            query.append(" FROM ").append(tableName);

            if (whereClause != null && !whereClause.isEmpty()) {
                query.append(" WHERE ").append(whereClause);
            }

            PreparedStatement stmt = conn.prepareStatement(query.toString());

            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }
            }

            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}