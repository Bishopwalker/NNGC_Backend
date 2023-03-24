package com.northernneckgarbage.nngc.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDTO {

    private Long id;

    private String fullName;
    private String email;
    private String phoneNumber;
    private AddressDTO address;


    private String role;
    private String stripeCustomerId;
    private boolean enabled;


}