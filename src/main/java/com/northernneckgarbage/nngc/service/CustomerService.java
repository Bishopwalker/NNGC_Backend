package com.northernneckgarbage.nngc.service;

import com.northernneckgarbage.nngc.dbConfig.ApiResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import org.springframework.data.domain.Page;


import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Customer addCustomer(Customer customer);


    Optional<Customer> findByEmail(String email);

    List<Customer> getCustomers();

   Page<Customer> getCustomersPage(int amount, int size);

    List<Customer> findCustomersWithSorting(String field, String direction);

    void deleteCustomer(Long id);

    ApiResponse<Customer> updateCustomer(Customer customer);

    Optional<Customer> getCustomerById(Long id);
}
