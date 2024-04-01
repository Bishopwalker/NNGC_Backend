package com.northernneckgarbage.repository;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Key;

@SpringBootApplication
public class NngcApplication {
    public static void main(String[] args) {
        SpringApplication.run(NngcApplication.class, args);
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
//System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));
    }}

 //Quickly run RouteService to test it
//Auto load the DB with 1000 customers, make up address, and complete customer information



