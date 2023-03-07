package com.northernneckgarbage.nngc.controller;

import com.northernneckgarbage.nngc.dbConfig.ApiResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller

@RequestMapping("api/nngc/")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class CustomerController {


 private final CustomerService customerService;



    @GetMapping("/customers")
    public List<Customer> getCustomers() {
        return customerService.getCustomers();
    }


    @PostMapping("/register")
    public String processRegister(@RequestBody  Customer customer) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
        log.info("New customer object created"+customer);
       customerService.addCustomer(customer);
        return "register_success";
    }

    @GetMapping("/customers/{email}")
    public Optional<Customer> getCustomer(@PathVariable String email) {
        return customerService.findByEmail(email);
    }

    @PutMapping("/customers/{id}")
    public ApiResponse<Customer> updateCustomer( @RequestBody Customer customer) {
        return customerService.updateCustomer(customer);
    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }


}
