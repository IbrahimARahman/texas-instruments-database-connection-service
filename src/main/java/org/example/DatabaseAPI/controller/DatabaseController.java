package org.example.DatabaseAPI.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@RestController
@RequestMapping("/api")
public class DatabaseController {
    private Connection connect() throws Exception {
        return DriverManager.getConnection("jdbc:oracle:thin:@//localhost:1521/ORCLPDB1", "DBAPI", "dbapipass");
    }

    @GetMapping("/listTables")
    public String listTables() {
        StringBuilder response = new StringBuilder();
        String query = "SELECT table_name, column_name, data_type, data_length " +
                "FROM user_tab_columns " +
                "ORDER BY table_name, column_id";

        try (Connection conn = connect()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            String currentTable = "";
            StringBuilder exampleValues = new StringBuilder();

            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                String columnName = rs.getString("COLUMN_NAME");
                String dataType = rs.getString("DATA_TYPE");
                int dataLength = rs.getInt("DATA_LENGTH");

                // Print table name only once when it changes
                if (!tableName.equals(currentTable)) {
                    if (!currentTable.isEmpty()) {
                        // Add example insert statement for the previous table
                        response.append("Example Insert: insert(\"")
                                .append(currentTable)
                                .append("\", ")
                                .append(exampleValues.append("));\n"));
                    }
                    currentTable = tableName;
                    exampleValues.setLength(0); // Reset for the new table
                    exampleValues.append("List.of(");
                    response.append("\nTable: ").append(tableName).append("\n");
                }

                // Append values for the example insert
                if (exampleValues.charAt(exampleValues.length() - 1) != '(') {
                    exampleValues.append(", ");
                }
                exampleValues.append(generateExampleValue(dataType, dataLength));

                // Append column details with Java/C++-like types
                response.append(columnName)
                        .append(" (")
                        .append(toJavaLikeType(dataType, dataLength))
                        .append(")\n");
            }

            // Add example insert statement for the last table
            if (!currentTable.isEmpty()) {
                response.append("Example Insert: insert(\"")
                        .append(currentTable)
                        .append("\", ")
                        .append(exampleValues.append("));\n"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
        return response.toString();
    }

    // Helper method to generate an example value based on the column's data type
    private String generateExampleValue(String dataType, int dataLength) {
        switch (dataType.toUpperCase()) {
            case "VARCHAR2":
                return "\"example\""; // String example
            case "NUMBER":
                return "123"; // Numeric example
            case "DATE":
                return "\"2023-01-01\""; // Date example
            default:
                return "\"unknown\""; // Default
        }
    }

    // Helper method to map SQL data types to Java-like types
    private String toJavaLikeType(String dataType, int dataLength) {
        switch (dataType.toUpperCase()) {
            case "VARCHAR2":
                return "String (" + dataLength + ")";
            case "NUMBER":
                return "int";
            case "DATE":
                return "Date";
            default:
                return "Object";
        }
    }
}
