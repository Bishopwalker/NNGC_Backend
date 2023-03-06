package com.northernneckgarbage.nngc.dbConfig;

import com.northernneckgarbage.nngc.stripe.StripePayment;
import com.stripe.model.Charge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeApiResponse<Type> {

    private String error;
    private StripePayment stripePayment;
    private Type data;
    private Charge charge;
}
