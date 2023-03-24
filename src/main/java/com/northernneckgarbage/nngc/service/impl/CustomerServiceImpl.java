package com.northernneckgarbage.nngc.service.impl;

import com.northernneckgarbage.nngc.dbConfig.ApiResponse;
import com.northernneckgarbage.nngc.dbConfig.StripeRegistrationResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.northernneckgarbage.nngc.service.CustomerService;
import com.northernneckgarbage.nngc.stripe.StripeService;
import com.stripe.exception.StripeException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final StripeService stripeService;
    @Override
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override

    public StripeRegistrationResponse<Optional<Customer>> findByEmail(String email) {
      Optional<Customer> customer = Optional.ofNullable(customerRepository.findByEmail(email).orElseThrow(()->
              new RuntimeException("Customer not found")));
        return StripeRegistrationResponse.<Optional<Customer>>builder()
                .message("Customer fetched successfully")
                .customerDTO(customer.get().toCustomerDTO())
                .build();
    }



    @Override
    public ApiResponse<List<Customer>> getCustomers() {
        log.info("Getting all customers");
        return ApiResponse.<List<Customer>>builder()
                .message("Customers fetched successfully")
                .customers(customerRepository.findAll())
                .build();

    }

    @Override
    public Page<Customer> getCustomersPage(int amount, int size) {
        return null;
    }

    @Override
    public List<Customer> findCustomersWithSorting(String field, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), field);
        return customerRepository.findAll(sort);
    }

    @Override
    public ApiResponse<String> deleteCustomer(Long id) {
        customerRepository.deleteById(id);
        return ApiResponse.<String>builder()
                .message("Customer deleted successfully")
                .build();
    }

    @Override
    public ApiResponse<Customer> updateCustomer(Customer customer, Long id) throws StripeException {
        var user = customerRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Customer not found"));


        var updateCustomer = Customer.builder()
                .id(id)
                .firstName(customer.getFirstName() == null ? user.getFirstName() : customer.getFirstName())
                .lastName(customer.getLastName() == null ? user.getLastName() : customer.getLastName())
                .email(customer.getEmail() == null ? user.getEmail() : customer.getEmail())
                .password(customer.getPassword() == null ?  user.getPassword() : passwordEncoder.encode(customer.getPassword()))
                .phone(customer.getPhone() == null ? user.getPhone() : customer.getPhone())
                .houseNumber(customer.getHouseNumber() == null ? user.getHouseNumber() : customer.getHouseNumber())
                .streetName(customer.getStreetName() == null ? user.getStreetName() : customer.getStreetName())
                .city(customer.getCity() == null ? user.getCity() : customer.getCity())
                .state(customer.getState() == null ? user.getState() : customer.getState())
                .zipCode(customer.getZipCode() == null ? user.getZipCode() : customer.getZipCode())
                .appUserRoles(customer.getAppUserRoles())
                .stripeCustomerId(user.getStripeCustomerId())
                .enabled(true)
                .build();
        log.info(updateCustomer.toString());
customerRepository.save(updateCustomer);
if(user.getStripeCustomerId() != null){
    log.info(updateCustomer.toString());
    stripeService.updateStripeCustomer(id);
    return ApiResponse.<Customer>builder()
            .customerDTO(updateCustomer.toCustomerDTO())
            .message("Stripe Customer updated successfully")
            .build();
}
        return ApiResponse.<Customer>builder()
                .customerDTO(updateCustomer.toCustomerDTO())
                .message("Customer updated successfully")
                .build();
    }

    @Override
    public  ApiResponse<Customer> getCustomerById(Long id) {
        var customer = customerRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Customer not found"));
        return  ApiResponse.<Customer>builder()
                .customerDTO(customer.toCustomerDTO())
                .message("Customer fetched successfully")
                .build();
    }
}
