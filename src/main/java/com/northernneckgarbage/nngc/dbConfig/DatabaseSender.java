package com.northernneckgarbage.nngc.dbConfig;

import com.github.javafaker.Faker;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class DatabaseSender {

     private final CustomerRepository customerRepository;

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        Faker faker = new Faker(new Locale("en-US"));

        for (int i = 0; i < 50; i++) {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String email = faker.internet().emailAddress();
            String phone = String.valueOf(faker.number().numberBetween(1000000000, 9999999999L));
            String password = "password1";
            String houseNumber = faker.address().buildingNumber();
            String streetName = faker.address().streetName();
            String city = faker.address().city();
            String state = "VA";  // Virginia
            String zipCode = faker.address().zipCodeByState(state);
            String county = "Northumberland County";

            Customer customer = Customer.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .phone(phone)
                    .password(password)
                    .houseNumber(houseNumber)
                    .streetName(streetName)
                    .city(city)
                    .state(state)
                    .zipCode(zipCode)
                    .county(county)
                    .latitude(Double.valueOf(faker.address().latitude()))
                    .longitude(Double.valueOf(faker.address().longitude()))
                    .build();

            customerRepository.save(customer);
        }
    }
}
