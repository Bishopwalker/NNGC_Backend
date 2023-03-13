package com.northernneckgarbage.nngc.service.impl;

import com.northernneckgarbage.nngc.dbConfig.ApiResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.northernneckgarbage.nngc.service.CustomerService;
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
    @Override
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override

    public Optional<Customer> findByEmail(String email) {
      Optional<Customer> customer = Optional.ofNullable(customerRepository.findByEmail(email).orElseThrow(()->
              new RuntimeException("Customer not found")));
        return customer;
    }


//    public List<Customer> getCustomers() {
//        log.info("Getting all customers");
//        return customerRepository.findAll();
//
//    }
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
    public ApiResponse<Customer> updateCustomer(Customer customer, Long id) {
        var user = customerRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Customer not found"));


        var updateCustomer = Customer.builder()
                .id(id)
                .firstName(customer.getFirstName() == null ? user.getFirstName() : customer.getFirstName())
                .lastName(customer.getLastName() == null ? user.getLastName() : customer.getLastName())
                .email(customer.getEmail() == null ? user.getEmail() : customer.getEmail())
                .password(customer.getPassword() == null ? passwordEncoder.encode(user.getPassword()) : customer.getPassword())
                .phone(customer.getPhone() == null ? user.getPhone() : customer.getPhone())
                .houseNumber(customer.getHouseNumber() == null ? user.getHouseNumber() : customer.getHouseNumber())
                .streetName(customer.getStreetName() == null ? user.getStreetName() : customer.getStreetName())
                .city(customer.getCity() == null ? user.getCity() : customer.getCity())
                .state(customer.getState() == null ? user.getState() : customer.getState())
                .zipCode(customer.getZipCode() == null ? user.getZipCode() : customer.getZipCode())
                .appUserRoles(customer.getAppUserRoles())
                .enabled(true)
                .build();
        log.info(updateCustomer.toString());
customerRepository.save(updateCustomer);
        return ApiResponse.<Customer>builder()
                .customerDTO(updateCustomer.toCustomerDTO())
                .message("Customer updated successfully")
                .build();
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        return Optional.empty();
    }
}
