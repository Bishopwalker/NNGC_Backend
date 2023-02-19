package com.northernneckgarbage.nngc.service;

import com.northernneckgarbage.nngc.entity.Customer;

import java.util.Optional;

public interface CustomerService {
    Customer addCustomer(Customer customer);

    Optional<Customer> findCustomerByEmail(String email);
}
