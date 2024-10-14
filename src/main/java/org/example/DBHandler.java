/*package org.example;

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

    public ResultSet execQuery(String query) {
        try (Connection conn = connect()) {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    abstract public void listTables();
    abstract String toJavaLikeType(String dataType, int dataLength);
    abstract String generateExampleValue(String dataType, int dataLength);

    abstract public void insert(String tableName, List<Object> values);
    abstract boolean isValueCompatibleWithType(Object value, String expectedDataType);

    abstract public void delete(String tableName, List<String> columns, List<Object> values);
}*/
package org.example;

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

    public ResultSet execQuery(String query) {
        try (Connection conn = connect()) {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public abstract void listTables();

    abstract String toJavaLikeType(String dataType, int dataLength);

    abstract String generateExampleValue(String dataType, int dataLength);

    public abstract void insert(String tableName, List<Object> values);

    abstract boolean isValueCompatibleWithType(Object value, String expectedDataType);

    public abstract void delete(String tableName, List<String> columns, List<Object> values);

    // New abstract select method
    public abstract ResultSet select(String tableName, List<String> columns, String whereClause, List<Object> params);
}
