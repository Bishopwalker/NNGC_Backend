package com.northernneckgarbage.nngc;

import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.northernneckgarbage.nngc.route4me.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NngcApplication {



    public static void main(String[] args) {
        SpringApplication.run(NngcApplication.class, args);
    }

 //Quickly run RouteService to test it


}
