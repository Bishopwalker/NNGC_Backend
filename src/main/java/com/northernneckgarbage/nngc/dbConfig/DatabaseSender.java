package com.northernneckgarbage.nngc.dbConfig;

import com.google.maps.errors.ApiException;
import com.northernneckgarbage.nngc.google.GeocodingService;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.northernneckgarbage.nngc.stripe.StripeService;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

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
      //  BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
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
        // }Charles Hale 596 Jessie Dupont Memorial Highway lorakfoley@gmail.com 804-238-4551
//        String phone =  "8042386065";
//        String password = passwordEncoder.encode("8042386065");
//        String houseNumber = "395";
//        String streetName = "Suggetts Point Rd";
//        String city = "Warsaw";
//        String state = "VA";  // Virginia
//        String zipCode = "22572";
//        String county = "Richmond County";
//        String service = "weekly_trash";
//        try {
//            Customer customer = Customer.builder()
//                    .firstName("Timmy")
//                    .lastName("Smith")
//                    .email("timmysmith3082@gmail.com")
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
//      stripeService.createStripeCustomersForAllUsers( );
//        geocodingService.updateAllUsersGeocodes();
  }
    }

