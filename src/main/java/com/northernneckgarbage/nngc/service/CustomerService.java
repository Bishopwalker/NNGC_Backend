package com.northernneckgarbage.nngc.service;

import com.northernneckgarbage.nngc.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Customer addCustomer(Customer customer);

    Optional<Customer> findByEmail(String email);

    List<Customer> getCustomers();
}
