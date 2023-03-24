package com.northernneckgarbage.nngc.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StripeDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private AddressDTO address;

}
