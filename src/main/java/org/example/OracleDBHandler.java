package org.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

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

    public void update(String updateQuery) {
        try (Connection conn = connect()) {
            Statement stmt = conn.createStatement();
            int rowsAffected = stmt.executeUpdate(updateQuery);

            System.out.println("Updated. Rows Affected: " + rowsAffected);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void remove(String deleteQuery) {
        try (Connection conn = connect()) {
            Statement stmt = conn.createStatement();
            int rowsAffected = stmt.executeUpdate(deleteQuery);

            System.out.println("Deleted. Rows Affected: " + rowsAffected);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void add(String insertQuery) {
        try (Connection conn = connect()) {
            Statement stmt = conn.createStatement();
            int rowsAffected = stmt.executeUpdate(insertQuery);

            System.out.println("Added. Rows Affected: " + rowsAffected);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
