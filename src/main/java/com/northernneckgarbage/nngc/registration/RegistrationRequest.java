package com.northernneckgarbage.nngc.registration;

import lombok.*;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class RegistrationRequest {

    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final String phone;
    private final String houseNumber;
    private final String streetName;
    private final String city;
    private final String state;
    private final String zipCode;
}
