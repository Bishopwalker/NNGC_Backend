package com.northernneckgarbage.nngc;
import java.util.List;
import java.util.Optional;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.northernneckgarbage.nngc.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class CustomerServiceImplTest {
    @Autowired
     CustomerService customerService;



    @Test
    public void testAddCustomer() {
        // Create a new customer object to add
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("johndoe@example.com");
        customer.setPassword("password");
        customer.setPhone("1234567890");
        customer.setHouseNumber("123");
        customer.setStreetName("Main St");
        customer.setCity("Anytown");
        customer.setState("CA");
        customer.setZipCode("12345");

        // Add the customer to the customerRepository
        Customer savedCustomer = customerService.addCustomer(customer);

        // Verify that the saved customer matches the original customer
        assertEquals(customer.getFirstName(), savedCustomer.getFirstName());
        assertEquals(customer.getLastName(), savedCustomer.getLastName());
        assertEquals(customer.getEmail(), savedCustomer.getEmail());
        assertEquals(customer.getPassword(), savedCustomer.getPassword());
        assertEquals(customer.getPhone(), savedCustomer.getPhone());
        assertEquals(customer.getHouseNumber(), savedCustomer.getHouseNumber());
        assertEquals(customer.getStreetName(), savedCustomer.getStreetName());
        assertEquals(customer.getCity(), savedCustomer.getCity());
        assertEquals(customer.getState(), savedCustomer.getState());
        assertEquals(customer.getZipCode(), savedCustomer.getZipCode());

        // Try adding a null customer and verify that an IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> {
            customerService.addCustomer(null);
        });
    }

    @Test
    void testFindByEmail() {
        // Add a new customer to the customerRepository
        Customer customer = new Customer();
        customer.setFirstName("Jane");
        customer.setLastName("Doe");
        customer.setEmail("janedoe@example.com");
        customer.setPassword("password");
        customer.setPhone("1234567890");
        customer.setHouseNumber("123");
        customer.setStreetName("Main St");
        customer.setCity("Anytown");
        customer.setState("CA");
        customer.setZipCode("12345");
        customerService.addCustomer(customer);

    }
    // Find the customer by email
    @Autowired
    CustomerRepository repo;
    Optional<Customer> foundCustomerOpt =repo.findByEmail("janedoe@example.com");

    // Verify that the customer is found and has the correct email

    Customer foundCustomer = foundCustomerOpt.get();

    // Try finding a customer with an invalid email and verify that a RuntimeException is thrown

    @Test
    void testGetCustomers() {
        // Add two customers to the customerRepository
        Customer customer1 = new Customer();
        customer1.setFirstName("John");
        customer1.setLastName("Doe");
        customer1.setEmail("johndoe@example.com");
        customer1.setPassword("password");
        customer1.setPhone("1234567890");
        customer1.setHouseNumber("123");
        customer1.setStreetName("Main St");
        customer1.setCity("Anytown");
        customer1.setState("CA");
        customer1.setZipCode("12345");
        customerService.addCustomer(customer1);

        Customer customer2 = new Customer();
        customer2.setFirstName("Jane");
        customer2.setLastName("Doe");
        customer2.setEmail("janedoe@example.com");
        customer2.setPassword("password");
        customer2.setPhone("1234567890");
        customer2.setHouseNumber("123");
        customer2.setStreetName("Main St");
        customer2.setCity("Anytown");
        customer2.setState("CA");
        customer2.setZipCode("12345");

    }
    // Add the customer to the customerRepository


}
