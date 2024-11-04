package org.example.DatabaseAPI.controller;

import org.example.DBHandler;
import org.example.OracleDBHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;

@RestController
@RequestMapping("/api")
public class DatabaseController {
    private final DBHandler DB;

    @Autowired
    public DatabaseController(OracleDBHandler dbHandler) {
        this.DB = dbHandler;
    }

    @PostMapping("/execQuery")
    public String execQuery(@RequestBody String query) {
        return DB.execQuery(query);
    }

    @GetMapping("/listTables")
    public String listTables() {
        System.out.println(DB.listTables());
        return DB.listTables();
    }

    @PostMapping("/insert")
    public ResponseEntity<String> insert(@RequestBody Map<String, Object> payload) {
        String tableName = (String) payload.get("tableName");
        List<Object> values = (List<Object>) payload.get("values");

        if (tableName == null || values == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
        }

        DB.insert(tableName, values);
        return ResponseEntity.status(HttpStatus.OK).body("Insert operation completed successfully");
    }

    @PostMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody Map<String, Object> payload) {
        String tableName = (String) payload.get("tableName");
        List<String> columns = (List<String>) payload.get("columns");
        List<Object> values = (List<Object>) payload.get("values");

        if (tableName == null || columns == null || values == null || columns.size() != values.size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
        }

        DB.delete(tableName, columns, values);
        return ResponseEntity.status(HttpStatus.OK).body("Delete operation completed successfully");
    }

    @PostMapping("/select")
    public ResponseEntity<String> select(@RequestBody Map<String, Object> payload) {
        String tableName = (String) payload.get("tableName");
        List<String> columns = (List<String>) payload.get("columns");
        String whereClause = (String) payload.get("whereClause");
        List<Object> params = (List<Object>) payload.get("params");

        if (tableName == null || (columns == null && whereClause == null && params == null)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
        }

        DB.select(tableName, columns, whereClause, params);
        return ResponseEntity.status(HttpStatus.OK).body("Select operation completed successfully");
    }
}
