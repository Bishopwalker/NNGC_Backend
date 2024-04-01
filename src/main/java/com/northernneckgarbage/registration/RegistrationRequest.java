package com.northernneckgarbage.registration;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

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
    private final String service;
    private final String zipCode;
}
