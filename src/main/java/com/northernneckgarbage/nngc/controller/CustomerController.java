package com.northernneckgarbage.nngc.controller;

import com.northernneckgarbage.nngc.dbConfig.ApiResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.service.CustomerService;
import com.northernneckgarbage.nngc.token.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
private final TokenRepository tokenRepository;


    @GetMapping("/customers")
   public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers(@RequestHeader("Authorization") String headers) {
       log.info(headers);
       var user=tokenRepository.findByToken(headers).get().getCustomer().getAppUserRoles();
         log.info(user.toString());
         if(user.toString().equals("ADMIN")){
                return ResponseEntity.ok(customerService.getCustomers());
         }
        return ResponseEntity.badRequest().body(ApiResponse.<List<Customer>>builder().message("You are not authorized to view this page").build());

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
    @GetMapping("/customers/{id}")
     public ResponseEntity<ApiResponse<Customer>> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
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
