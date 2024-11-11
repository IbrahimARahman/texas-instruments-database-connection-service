package org.example;
public class Result {
    private String status;
    private String message;
    private String data;  // Optional, for SELECT query results

    public Result(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public Result(String status, String message, String data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }
}
