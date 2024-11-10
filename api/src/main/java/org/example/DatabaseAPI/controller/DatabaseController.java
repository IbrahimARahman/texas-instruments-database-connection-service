package org.example.DatabaseAPI.controller;

import org.example.DBHandler;
import org.example.OracleDBHandler;
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
    public ResponseEntity<Map<String, Object>> execQuery(@RequestBody String query) {
        String result = DB.execQuery(query);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("result", result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/listTables")
    public ResponseEntity<Map<String, Object>> listTables() {
        String tables = DB.listTables();
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("tables", tables);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/insert")
    public ResponseEntity<Map<String, Object>> insert(@RequestBody Map<String, Object> payload) {
        String tableName = (String) payload.get("tableName");
        List<Object> values = (List<Object>) payload.get("values");

        if (tableName == null || values == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Invalid input");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        DB.insert(tableName, values);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Insert operation completed successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> delete(@RequestBody Map<String, Object> payload) {
        String tableName = (String) payload.get("tableName");
        List<String> columns = (List<String>) payload.get("columns");
        List<Object> values = (List<Object>) payload.get("values");

        if (tableName == null || columns == null || values == null || columns.size() != values.size()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Invalid input");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        DB.delete(tableName, columns, values);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Delete operation completed successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/select")
    public ResponseEntity<Map<String, Object>> select(@RequestBody Map<String, Object> payload) {
        String tableName = (String) payload.get("tableName");
        List<String> columns = (List<String>) payload.get("columns");
        String whereClause = (String) payload.get("whereClause");
        List<Object> params = (List<Object>) payload.get("params");

        if (tableName == null || columns == null || whereClause == null || params == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Invalid input");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        String result = DB.select(tableName, columns, whereClause, params);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("result", result);
        return ResponseEntity.ok(response);
    }
}
