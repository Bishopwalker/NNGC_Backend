package com.northernneckgarbage.nngc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class NngcApplication {



    public static void main(String[] args) {
        SpringApplication.run(NngcApplication.class, args);
    }

}
