package com.northernneckgarbage.nngc.repository;

import com.northernneckgarbage.nngc.entity.Customer;

import com.northernneckgarbage.nngc.registration.RegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer save(Customer customer);


    Customer save(RegistrationRequest request);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByPhone(String phone);

}
