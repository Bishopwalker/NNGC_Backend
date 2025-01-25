package com.northernneckgarbage.nngc;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;





@SpringBootApplication
public class NngcApplication {
    public static void main(String[] args) {
        SpringApplication.run(NngcApplication.class, args);
    }
}
