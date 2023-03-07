package com.northernneckgarbage.nngc.service.impl;

import com.northernneckgarbage.nngc.dbConfig.ApiResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.entity.dto.CustomerDTO;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.northernneckgarbage.nngc.service.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.List;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
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

    @Override
    public List<Customer> getCustomers() {
        log.info("Getting all customers");
        return customerRepository.findAll();

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
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    @Override
    public ApiResponse<Customer> updateCustomer(Customer customer) {
         customerRepository.findById(customer.getId()).orElseThrow(()->
                new RuntimeException("Customer not found"));
        var updateCustomer = Customer.builder()
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .password(customer.getPassword())
                .phone(customer.getPhone())
                .houseNumber(customer.getHouseNumber())
                .streetName(customer.getStreetName())
                .city(customer.getCity())
                .state(customer.getState())
                .zipCode(customer.getZipCode())
                .build();
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
