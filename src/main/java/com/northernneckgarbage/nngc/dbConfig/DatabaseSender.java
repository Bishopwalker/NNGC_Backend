package com.northernneckgarbage.nngc.dbConfig;

import com.github.javafaker.Faker;
import com.google.maps.errors.ApiException;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.google.GeocodingService;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.northernneckgarbage.nngc.roles.AppUserRoles;
import com.northernneckgarbage.nngc.stripe.StripeService;
import com.stripe.exception.StripeException;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class DatabaseSender {
//
//     private final CustomerRepository customerRepository;
//private final GeocodingService geocodingService;
//private final StripeService stripeService;
//
//    @EventListener
//    public void seed(ContextRefreshedEvent event) throws IOException, InterruptedException, ApiException, java.io.IOException, StripeException {
//
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


        //  String county = "Northumberland County";
//
//            Customer customer = Customer.builder()
//
//                    .firstName("NNGC")
//                    .lastName("ADMIN")
//                    .appUserRoles(AppUserRoles.ADMIN)
//                    .email("bishop@northernneckgarbage.com")
//                    .enabled(true)
//                    .phone("8042200029")
//                    .password(passwordEncoder.encode("lottsburg1"))
//                    .houseNumber("164")
//                    .streetName("Cellar Haven Lane")
//                    .city("Lottsburgh")
//                    .state("VA")
//                    .zipCode("22511")
//                    .county("Northumberland County")
//                    .build();
//customerRepository.save(customer);
//            Customer customer1 = Customer.builder()
//                    .id(2L)
//                    .firstName("Bart")
//                    .lastName("Morrison")
//                    .email("Barton@northernneckchristian.org")
//                    .phone("8044023353")
//                    .password(passwordEncoder.encode("8044023353"))
//                    .houseNumber("506")
//                    .streetName("Fountain Gate Rd")
//                    .city("Heathsville")
//                    .state("VA")
//                    .zipCode("22473")
//                    .county(county)
//                    .build();
//        customerRepository.save(customer1);
//
//            Customer customer2 = Customer.builder()
//                    .id(3L)
//                    .firstName("Dee")
//                    .lastName("King")
//                    .email("mormor2day@yahoo.com")
//                    .phone("5408098548")
//                    .password(passwordEncoder.encode("5408098548"))
//                    .houseNumber("799")
//                    .streetName("Remo Rd")
//                    .city("Heathsville")
//                    .state("VA")
//                    .zipCode("22473")
//                    .county(county)
//                    .build();
//            customerRepository.save(customer2);






      //  stripeService.createStripeCustomersForAllUsers( );
// geocodingService.updateAllUsersGeocodes();
// }
//}
