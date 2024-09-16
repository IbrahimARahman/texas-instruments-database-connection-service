package org.example;

import java.sql.*;


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

    public ResultSet execQuery(String query) {
        try (Connection conn = connect()) {

            Statement stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    abstract public void listTables();
}
