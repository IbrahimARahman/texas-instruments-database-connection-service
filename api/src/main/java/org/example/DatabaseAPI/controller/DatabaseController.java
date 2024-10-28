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
}
