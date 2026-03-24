package com.northernneckgarbage.nngc.stripe.transaction;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillingDetails {
    private String email;
    private String name;
    private Address address;
}
