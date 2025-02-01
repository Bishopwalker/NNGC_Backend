package com.northernneckgarbage.nngc;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.sql.DataSource;

@SpringBootApplication
public class NngcApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(NngcApplication.class, args);

        // Add shutdown hook to clean up connections
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DataSource dataSource = context.getBean(DataSource.class);
            if (dataSource instanceof HikariDataSource hikariDataSource) {
                hikariDataSource.close();
            }
        }));
    }
}