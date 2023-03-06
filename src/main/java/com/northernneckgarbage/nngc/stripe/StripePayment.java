package com.northernneckgarbage.nngc.stripe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripePayment {
 public enum Currency {
     USD, EUR
 }
 private String description;
    private int amount;
    private Currency currency;
private String stripeEmail;
    private String stripeToken;
}
