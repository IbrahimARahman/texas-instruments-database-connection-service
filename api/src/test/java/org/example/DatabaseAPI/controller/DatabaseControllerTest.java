package org.example.DatabaseAPI.controller;

import org.example.OracleDBHandler;
import org.example.Result;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DatabaseController.class)
class DatabaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OracleDBHandler oracleDBHandler;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public OracleDBHandler oracleDBHandler() {
            // Provide a mock implementation or supply required constructor arguments
            return Mockito.spy(new OracleDBHandler("jdbc:test-url", "test-user", "test-pass"));
        }
    }

    @Test
    void testExecQuery() throws Exception {
        // Mock behavior for execQuery
        Mockito.doReturn(new Result("success", "Query executed successfully", "data"))
                .when(oracleDBHandler).execQuery(any(String.class));

        // Perform POST request
        mockMvc.perform(post("/api/execQuery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"SELECT * FROM PEOPLE\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Query executed successfully"))
                .andExpect(jsonPath("$.data").value("data"));
    }

    @Test
    void testCreateTable() throws Exception {
        // Mock behavior for createTable
        Mockito.doReturn(new Result("success", "Table created successfully"))
                .when(oracleDBHandler).createTable(any(String.class));

        // Perform POST request
        mockMvc.perform(post("/api/createTable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"CREATE TABLE TEST (id INT)\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Table created successfully"));
    }

    @Test
    void testListTables() throws Exception {
        // Mock behavior for listTables
        Mockito.doReturn(new Result("success", "Tables retrieved successfully", "table1, table2"))
                .when(oracleDBHandler).listTables();

        // Perform GET request
        mockMvc.perform(get("/api/listTables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Tables retrieved successfully"))
                .andExpect(jsonPath("$.data").value("table1, table2"));
    }

    @Test
    void testInsert() throws Exception {
        // Mock behavior for insert
        Mockito.doReturn(new Result("success", "Insert successful"))
                .when(oracleDBHandler).insert(any(String.class), any(List.class));

        // Perform POST request
        mockMvc.perform(post("/api/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tableName\": \"PEOPLE\", \"values\": [{\"name\": \"John Doe\", \"age\": 30}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Insert successful"));
    }

    @Test
    void testDelete() throws Exception {
        // Mock behavior for delete
        Mockito.doReturn(new Result("success", "Delete successful"))
                .when(oracleDBHandler).delete(any(String.class), any(List.class), any(List.class));

        // Perform POST request
        mockMvc.perform(post("/api/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tableName\": \"PEOPLE\", \"columns\": [\"name\"], \"values\": [\"John Doe\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Delete successful"));
    }

    @Test
    void testSelect() throws Exception {
        // Mock behavior for select
        Mockito.doReturn(new Result("success", "Select successful", "selected data"))
                .when(oracleDBHandler).select(any(String.class), any(List.class), any(String.class), any(List.class));

        // Perform POST request
        mockMvc.perform(post("/api/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tableName\": \"PEOPLE\", \"columns\": [\"name\", \"age\"], \"whereClause\": \"age > ?\", \"params\": [25]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Select successful"))
                .andExpect(jsonPath("$.data").value("selected data"));
    }
}
