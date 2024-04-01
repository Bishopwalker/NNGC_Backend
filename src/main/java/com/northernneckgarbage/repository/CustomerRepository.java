package com.northernneckgarbage.repository;

import com.northernneckgarbage.entity.Customer;
import com.northernneckgarbage.registration.RegistrationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer save(Customer customer);

    @Query("SELECT c FROM Customer c WHERE c.enabled = true")
    List<Customer> findEnabledCustomers(PageRequest pageRequest);
    Customer save(RegistrationRequest request);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByPhone(String phone);


    @Query("SELECT c FROM Customer c WHERE c.stripeCustomerId = ?1")
    Optional<Customer> locateByStripeID(String stripeCustomerId);

    Page<Customer> findEnabledCustomersByCounty(Pageable pageable, String county);

    long countByCounty(String county);

}
