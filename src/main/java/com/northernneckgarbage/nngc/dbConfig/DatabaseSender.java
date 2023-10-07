package com.northernneckgarbage.nngc.dbConfig;

//@Component
//@RequiredArgsConstructor
//public class DatabaseSender {
//
//     private final CustomerRepository customerRepository;
//private final GeocodingService geocodingService;
//private final StripeService stripeService;
//    @EventListener
//    public void seed(ContextRefreshedEvent event) throws IOException, InterruptedException, ApiException {
//        Faker faker = new Faker(new Locale("en-US"));
//
//        for (int i = 0; i < 100; i++) {
//            String firstName = faker.name().firstName();
//            String lastName = faker.name().lastName();
//            String email = faker.internet().emailAddress();
//            String phone = String.valueOf(faker.number().numberBetween(1000000000, 9999999999L));
//            String password = "password1";
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
//            stripeService.createStripeCustomersForAllUsers( );
//            geocodingService.updateAllUsersGeocodes();
//        }
//    }
//}
