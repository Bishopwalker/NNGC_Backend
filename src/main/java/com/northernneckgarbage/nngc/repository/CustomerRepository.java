package com.northernneckgarbage.nngc.repository;

import com.northernneckgarbage.nngc.entity.Customer;

import com.northernneckgarbage.nngc.registration.RegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer save(Customer customer);


    Customer save(RegistrationRequest request);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByPhone(String phone);

//    @Modifying
//    @Query( "UPDATE Customer c SET c.firstName = :firstName, c.lastName = :lastName," +
//            " c.email = :email, c.phone = :phone, c.houseNumber = :houseNumber, c.streetName = :streetName," +
//            " c.city = :city, c.state = :state,c.zipCode = :zipCode WHERE c.id = :id" )
//   void updateCustomer(Customer customer);


}
