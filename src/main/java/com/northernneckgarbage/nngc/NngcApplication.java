package com.northernneckgarbage.nngc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NngcApplication {
    public static void main(String[] args) {
      SpringApplication.run(NngcApplication.class);

//        // Add shutdown hook to clean up connections
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            DataSource dataSource = context.getBean(DataSource.class);
//            if (dataSource instanceof HikariDataSource hikariDataSource) {
//                hikariDataSource.close();
//            }
//        }));
    }
}