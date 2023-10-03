package com.northernneckgarbage.nngc;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Key;
import java.util.Base64;

@SpringBootApplication
public class NngcApplication {
    public static void main(String[] args) {
        SpringApplication.run(NngcApplication.class, args);


    }}

 //Quickly run RouteService to test it
//Auto load the DB with 1000 customers, make up address, and complete customer information



