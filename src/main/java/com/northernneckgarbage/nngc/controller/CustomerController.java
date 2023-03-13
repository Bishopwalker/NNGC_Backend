package com.northernneckgarbage.nngc.controller;

import com.northernneckgarbage.nngc.dbConfig.ApiResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.hibernate.tool.schema.extract.internal.IndexInformationImpl.builder;

@Slf4j
@Controller
@RequestMapping("api/nngc/")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class CustomerController {


 private final CustomerService customerService;



    @GetMapping("/customers")
   public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getCustomers());

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


//    public ApiResponse<Customer> updateCustomer( @RequestBody Customer customer, @PathVariable Long id) {
//        return customerService.updateCustomer(customer, id);
//    }
    @PutMapping("/customers/{id}")
    public ResponseEntity<ApiResponse<Customer>> updateCustomer(@RequestBody Customer customer, @PathVariable Long id) {
        return ResponseEntity.ok(customerService.updateCustomer(customer, id));
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<ApiResponse> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return   ResponseEntity.ok(ApiResponse.builder()
                .message("Customer deleted")
                .build());
    }


}
