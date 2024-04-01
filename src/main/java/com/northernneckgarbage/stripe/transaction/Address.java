package com.northernneckgarbage.stripe.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String city;
    private String country;
    private String line1;
    private String postal_code;
    private String state;
}
