package org.example.DatabaseAPI.controller;

import org.example.DBHandler;
import org.example.OracleDBHandler;
import org.example.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DatabaseController {
    private final DBHandler DB;

    @Autowired
    public DatabaseController(OracleDBHandler dbHandler) {
        this.DB = dbHandler;
    }

    @PostMapping("/execQuery")
    public ResponseEntity<Map<String, Object>> execQuery(@RequestBody Map<String, String> request) {
        // Extract query string from request body
        String query = request.get("query");

        if (query == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Query string is missing or empty"
            ));
        }

        // Call execQuery with the extracted query string
        Result result = DB.execQuery(query);
        Map<String, Object> response = new HashMap<>();
        response.put("status", result.getStatus());
        response.put("message", result.getMessage());
        if (result.getData() != null) {
            response.put("data", result.getData());
        }
        return ResponseEntity.ok(response);
    }


    @PostMapping("/createTable")
    public ResponseEntity<Map<String, Object>> createTable(@RequestBody String sqlStr) {
        Result result = DB.createTable(sqlStr);
        Map<String, Object> response = new HashMap<>();
        response.put("status", result.getStatus());
        response.put("message", result.getMessage());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/listTables")
    public ResponseEntity<Map<String, Object>> listTables() {
        Result result = DB.listTables();
        Map<String, Object> response = new HashMap<>();
        response.put("status", result.getStatus());
        response.put("message", result.getMessage());
        if (result.getData() != null) response.put("data", result.getData());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/insert")
    public ResponseEntity<Map<String, Object>> insert(@RequestBody Map<String, Object> payload) {
        String tableName = (String) payload.get("tableName");
        List<Object> values = (List<Object>) payload.get("values");

        if (tableName == null || values == null) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid input"));
        }

        Result result = DB.insert(tableName, values);
        return ResponseEntity.ok(Map.of("status", result.getStatus(), "message", result.getMessage()));
    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> delete(@RequestBody Map<String, Object> payload) {
        String tableName = (String) payload.get("tableName");
        List<String> columns = (List<String>) payload.get("columns");
        List<Object> values = (List<Object>) payload.get("values");

        if (tableName == null || columns == null || values == null || columns.size() != values.size()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid input"));
        }

        Result result = DB.delete(tableName, columns, values);
        Map<String, Object> response = new HashMap<>();
        response.put("status", result.getStatus());
        response.put("message", result.getMessage());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/select")
    public ResponseEntity<Map<String, Object>> select(@RequestBody Map<String, Object> payload) {
        String tableName = (String) payload.get("tableName");
        List<String> columns = (List<String>) payload.get("columns");
        String whereClause = (String) payload.get("whereClause");
        List<Object> params = (List<Object>) payload.get("params");

        if (tableName == null || columns == null || whereClause == null || params == null) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid input"));
        }

        Result result = DB.select(tableName, columns, whereClause, params);
        Map<String, Object> response = new HashMap<>();
        response.put("status", result.getStatus());
        response.put("message", result.getMessage());
        if (result.getData() != null) {
            response.put("data", result.getData());
        }
        return ResponseEntity.ok(response);
    }

}
