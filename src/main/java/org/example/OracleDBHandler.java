package org.example;

import java.sql.*;
import java.util.List;

public class OracleDBHandler extends DBHandler {
    public OracleDBHandler(String url, String user, String password) {
        super(url, user, password);
    }

    public void listTables(){
        String query = "SELECT table_name, column_name, data_type, data_length " +
                "FROM user_tab_columns " +
                "ORDER BY table_name, column_id";
        try (Connection conn = connect()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            String currentTable = "";
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                String columnName = rs.getString("COLUMN_NAME");
                String dataType = rs.getString("DATA_TYPE");
                int dataLength = rs.getInt("DATA_LENGTH");

                // Print table name only once when it changes
                if (!tableName.equals(currentTable)) {
                    System.out.println("\nTable: " + tableName);
                    currentTable = tableName;
                }

                // Print column details
                System.out.println("    Column: " + columnName + " | Data Type: " + dataType + " | Length: " + dataLength);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insert(String tableName, List<Object> values) {
        String checkTableQuery = "SELECT COUNT(*) FROM user_tables WHERE table_name = UPPER(?)";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkTableQuery)) {
            // checking if table exists
            checkStmt.setString(1, tableName);
            ResultSet tableExistsResult = checkStmt.executeQuery();
            if (tableExistsResult.next() && tableExistsResult.getInt(1) == 0) {
                System.out.println("Table " + tableName + " does not exist.");
                return;
            }

            // getting the schema info
            String getColumnsQuery = "SELECT column_name FROM user_tab_columns WHERE table_name = UPPER(?) ORDER BY column_id";
            try (PreparedStatement columnsStmt = conn.prepareStatement(getColumnsQuery)) {
                columnsStmt.setString(1, tableName);
                ResultSet columnsResult = columnsStmt.executeQuery();

                StringBuilder columns = new StringBuilder();
                StringBuilder placeholders = new StringBuilder();
                int columnCount = 0;

                while (columnsResult.next()) {
                    if (columnCount > 0) {
                        columns.append(", ");
                        placeholders.append(", ");
                    }
                    columns.append(columnsResult.getString("COLUMN_NAME"));
                    placeholders.append("?");
                    columnCount++;
                }

                // verifying schema and list allign
                if (columnCount != values.size()) {
                    System.out.println("Number of values does not match the number of columns.");
                    return;
                }

                // inserting
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
            System.out.println("Error inserting. Please make sure the datatypes and number of columns are correct.");
        }
    }
}
