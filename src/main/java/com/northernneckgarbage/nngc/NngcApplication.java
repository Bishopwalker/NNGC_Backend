package com.northernneckgarbage.nngc;


import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class NngcApplication {

    @Autowired
    private HikariDataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(NngcApplication.class, args);
    }

    @PreDestroy
    public void shutdownDataSource() {
        if (dataSource != null) {
            dataSource.close();
        }


//System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));
    }
}

 //Quickly run RouteService to test it
//Auto load the DB with 1000 customers, make up address, and complete customer information



