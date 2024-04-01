package com.northernneckgarbage.entity.dto;

import com.northernneckgarbage.roles.AppUserRoles;
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


    private AppUserRoles role;
    private String stripeCustomerId;
    private String geoLocation;
    private boolean enabled;
    private String receiptURL;
    private String invoiceURL;

    private boolean changePassword;

    private String service;

}