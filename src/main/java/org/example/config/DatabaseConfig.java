package org.example.config;

import org.example.OracleDBHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DatabaseConfig {

    @Bean
    public OracleDBHandler OracleDBHandler(
            @Value("${db.url}") String url,
            @Value("${db.username}") String user,
            @Value("${db.password}") String password) {
        return new OracleDBHandler(url, user, password);
    }
}
