package com.northernneckgarbage.nngc.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDTO {

    private String firstName;
    private String lastName;
    private String fullName;
    private String email;

    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String role;
    private boolean enabled;


}