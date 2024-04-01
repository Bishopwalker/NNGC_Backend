package com.northernneckgarbage.stripe.transaction;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    private String brand;
    private String country;
    private Integer exp_month;
    private Integer exp_year;
    private String last4;
}
