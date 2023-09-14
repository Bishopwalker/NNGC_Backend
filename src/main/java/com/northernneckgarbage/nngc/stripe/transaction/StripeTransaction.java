package com.northernneckgarbage.nngc.stripe.transaction;


import com.northernneckgarbage.nngc.entity.StripeTransactions;
import com.stripe.model.Charge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeTransaction {
    private String id;
    private Integer amount;
    private String currency;
    private String description;
    private Boolean captured;
    private String status;
    private BillingDetails billingDetails;
    private  PaymentMethodDetails paymentMethodDetails;


}
