package com.northernneckgarbage.nngc.dbConfig;

import com.github.javafaker.Faker;
import com.google.maps.errors.ApiException;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.google.GeocodingService;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.northernneckgarbage.nngc.stripe.StripeService;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSender {

    private final CustomerRepository customerRepository;
    private final GeocodingService geocodingService;
    private final StripeService stripeService;

    @EventListener
    public void seed(ContextRefreshedEvent event) throws IOException, InterruptedException, ApiException, java.io.IOException {
        //Faker faker = new Faker(new Locale("en-US"));
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        for (int i = 0; i < 50; i++) {
//            String firstName = faker.name().firstName();
//            String lastName = faker.name().lastName();
//            String email = faker.internet().emailAddress();
//            String phone = String.valueOf(faker.number().numberBetween(1000000000, 9999999999L));
//            String password = passwordEncoder.encode("password1");
//            String houseNumber = faker.address().buildingNumber();
//            String streetName = faker.address().streetName();
//            String city = faker.address().city();
//            String state = "VA";  // Virginia
//            String zipCode = faker.address().zipCodeByState("VA");
//            String county = "Northumberland County";
//
//            Customer customer = Customer.builder()
//                    .firstName(firstName)
//                    .lastName(lastName)
//                    .email(email)
//                    .phone(phone)
//                    .password(password)
//                    .houseNumber(houseNumber)
//                    .streetName(streetName)
//                    .city(city)
//                    .state(state)
//                    .zipCode(zipCode)
//                    .county(county)
//                    .latitude(Double.valueOf(faker.address().latitude()))
//                    .longitude(Double.valueOf(faker.address().longitude()))
//                    .build();
//
//            customerRepository.save(customer);
//
//        }
//        String phone =  "804-580-8954";
//        String password = passwordEncoder.encode("8045808954");
//        String houseNumber = "451";
//        String streetName = "Witch Duck Lane";
//        String city = "Heathsville";
//        String state = "VA";  // Virginia
//        String zipCode = "22473";
//        String county = "Northumberland County";
//        String service = "weekly_trash";
//        try {
//            Customer customer = Customer.builder()
//                    .firstName("Alfred")
//                    .lastName("Cappelli")
//                    .email("alcap43@gmail.com")
//                    .phone(phone)
//                    .password(password)
//                    .houseNumber(houseNumber)
//                    .streetName(streetName)
//                    .city(city)
//                    .state(state)
//                    .zipCode(zipCode)
//                    .county(county)
//                    .service(service)
//                    .enabled(true)
//                    .build();
//            customerRepository.save(customer);
//
//
//        }catch (Exception e) {
//            log.info("Customer already exists");
//        }
//
//       // stripeService.createStripeCustomersForAllUsers( );
//        geocodingService.updateAllUsersGeocodes();
  }
    }

