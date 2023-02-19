package com.northernneckgarbage.nngc.repository;

import com.northernneckgarbage.nngc.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer save(Customer customer);

    Optional<Customer> findCustomerByEmail(String email);
}
