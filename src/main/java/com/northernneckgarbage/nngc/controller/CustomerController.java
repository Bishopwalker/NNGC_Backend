package com.northernneckgarbage.nngc.controller;

import com.northernneckgarbage.nngc.dbConfig.ApiResponse;
import com.northernneckgarbage.nngc.dbConfig.StripeRegistrationResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.repository.TokenRepository;
import com.northernneckgarbage.nngc.service.CustomerService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {


 private final CustomerService customerService;
private final TokenRepository tokenRepository;


@PostMapping("/add_bulk")
public ResponseEntity addBulkCustomers(@RequestBody List<Customer> customers) {
    customerService.addBulkCustomers(customers);
return ResponseEntity.ok(customers.size() + "Customers added: " + customers);

}

    @GetMapping("/stripe_id/{id}")
    public ResponseEntity<ApiResponse<Customer>> getCustomerByStripeId(@PathVariable String id) {

        //customerService.getCustomerByStripeId(id);
        return  ResponseEntity.ok(
                ApiResponse.<Customer>builder()
                        .message("Customer found")
                        .customerDTO(customerService.getCustomerByStripeId(id).getCustomerDTO())
                        .build());
    }


    @GetMapping("/customers")
   public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers(@RequestHeader("Authorization") String headers) {
       log.info(headers);
       var user=tokenRepository.findByToken(headers).get().getCustomer().getAppUserRoles();
        if(user==null){
            return ResponseEntity.badRequest().body(ApiResponse.<List<Customer>>builder().message("You are not authorized to view this page").build());
        }
         if(user.toString().equals("ADMIN")){
                return ResponseEntity.ok(customerService.getCustomers());
         }
        return ResponseEntity.badRequest().body(ApiResponse.<List<Customer>>builder().message("You are not authorized to view this page").build());

    }
    @GetMapping("/customers/")
    public ResponseEntity<StripeRegistrationResponse<Optional<Customer>>> getCustomerByEmail(@RequestHeader("Authorization") String headers, @RequestParam String email){
        log.info(headers);
        var user = tokenRepository.findByToken(headers).get().getCustomer().getAppUserRoles();
        if(user==null){
            return ResponseEntity.badRequest().body(StripeRegistrationResponse.<Optional<Customer>>builder().message("You are not authorized to view this page").build());
        }
        log.info(user.toString());
        if(user.toString().equals("ADMIN") || user.toString().equals("STRIPE_CUSTOMER")){
            return ResponseEntity.ok(customerService.findByEmail(email));
        }
        return ResponseEntity.badRequest().body(StripeRegistrationResponse.<Optional<Customer>>builder().message("You are not authorized to view this page").build());
    }


    @GetMapping("/customers/{id}")
    public ResponseEntity<ApiResponse<Customer>> getCustomerById(@RequestHeader("Authorization") String headers, @PathVariable Long id) {
        log.info(headers);
        var tokenOpt = tokenRepository.findByToken(headers);
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.<Customer>builder().message("Invalid or missing token").build());
        }

        var user = tokenOpt.get().getCustomer();
        if (user == null) {
            return ResponseEntity.badRequest().body(ApiResponse.<Customer>builder().message("You are not authorized to view this page").build());
        }
        log.info(user.toString());
        if ("ADMIN".equals(user.getAppUserRoles().toString()) || user.getId().equals(id)) {
            var customer = customerService.getCustomerById(id);
            if (customer == null) {
                return ResponseEntity.badRequest().body(ApiResponse.<Customer>builder().message("Customer not found").build());
            }
            return ResponseEntity.ok(ApiResponse.<Customer>builder()
                    .customerDTO(customer.getCustomerDTO())
                    .message("Customer found")
                    .token(customer.getToken())
                    .build());
        }
        return ResponseEntity.badRequest().body(ApiResponse.<Customer>builder().message("You are not authorized to view this page").build());
    }


@GetMapping("/update_stripe")
public void updateStripeForAllUsers() throws StripeException {
    customerService.updateStripeForAllUsers();
}


    @PutMapping("/customers/{id}")
    public ResponseEntity<ApiResponse<Customer>> updateCustomer(@RequestHeader("Authorization") String headers, @RequestBody Customer customer, @PathVariable Long id) throws StripeException {

        var user = tokenRepository.findByToken(headers).get().getCustomer();
log.info(user.toString());
        if(user==null){
            return ResponseEntity.badRequest().body(ApiResponse.<Customer>builder().message("You are not authorized to view this page").build());
        }
       if (user.getAppUserRoles().toString().equals("ADMIN") || user.getId()==id){
           return ResponseEntity.ok(customerService.updateCustomer(customer, id));
         }
        return ResponseEntity.badRequest().body(ApiResponse.<Customer>builder().message("You are not authorized to view this page").build());
    }
@PutMapping("/customers/email/{email}")
public ResponseEntity<ApiResponse<Customer>> updateCustomer( @RequestBody Customer customer, @PathVariable String email) throws StripeException, IOException {
   log.info(email);
    var user = customerService.findByEmail(email).getCustomerDTO();

    if (user.getEmail().equals(email)){
        return ResponseEntity.ok(customerService.updateCustomer(customer, email));
    }
    return ResponseEntity.badRequest().body(ApiResponse.<Customer>builder().message("You are not authorized to view this page").build());


}
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<ApiResponse> deleteCustomer(@RequestHeader("Authorization") String headers, @PathVariable Long id) {
        var user = tokenRepository.findByToken(headers).get().getCustomer();
        if(user==null){
            return ResponseEntity.badRequest().body(ApiResponse.<Customer>builder().message("You are not authorized to view this page").build());
        }
        if (user.getAppUserRoles().toString().equals("ADMIN") || user.getId()==id){
            customerService.deleteCustomer(id);
            return ResponseEntity.ok(ApiResponse.builder()
                    .message("Customer deleted")
                    .build());
        }

        return   ResponseEntity.ok(ApiResponse.builder()
                .message("Customer not deleted because you don't have permission to delete this customer")
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity processRegister(@RequestHeader("Authorization") String headers, @RequestBody  Customer customer) {
        var user = tokenRepository.findByToken(headers).get().getCustomer().getAppUserRoles();
        if(user==null || !user.toString().equals("ADMIN")){
            return ResponseEntity.badRequest().body(ApiResponse.<Customer>builder().message("You Don't have authorization to add a new customers").build());
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);

        log.info("New customer object created"+customer);
        customerService.addCustomer(customer);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Customer added")
                .build());
    }




}
