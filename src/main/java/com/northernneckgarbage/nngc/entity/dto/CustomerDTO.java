package com.northernneckgarbage.nngc.entity.dto;

import com.northernneckgarbage.nngc.roles.AppUserRoles;
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


}