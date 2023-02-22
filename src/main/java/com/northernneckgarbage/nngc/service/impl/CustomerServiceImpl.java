package com.northernneckgarbage.nngc.service.impl;

import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.northernneckgarbage.nngc.service.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
