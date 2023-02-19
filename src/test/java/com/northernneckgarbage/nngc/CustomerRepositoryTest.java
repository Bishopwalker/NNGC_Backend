package com.northernneckgarbage.nngc;

import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.springframework.test.web.servlet.result.StatusResultMatchersExtensionsKt.isEqualTo;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTest {


    private final TestEntityManager entityManager;

    private CustomerRepositoryTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }
    @Autowired
    private CustomerRepository repo;

    @Test
    public void testCreateCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("e.mail.com");
        customer.setPhone("1234567890");
        customer.setHouseNumber("123");
        customer.setStreetName("Main Street");
        customer.setCity("City");
        customer.setState("VA");
        customer.setZipCode("12345");
        customer.setCounty("County");
        customer.setNotes("Notes");

        Customer savedCustomer = repo.save(customer);

        Customer existCustomer = entityManager.find(Customer.class, savedCustomer.getId());
        assert existCustomer.getEmail() == customer.getEmail();
//        assertThat(existCustomer.getEmail()).isEqualTo(customer.getEmail());
    }
}
