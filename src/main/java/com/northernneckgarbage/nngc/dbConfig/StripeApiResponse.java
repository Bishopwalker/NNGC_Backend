package com.northernneckgarbage.nngc.dbConfig;

import com.google.api.services.people.v1.model.Url;
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
public class StripeApiResponse<T> {

    private String error;
    private StripePayment stripePayment;
    private T data;
    private Charge charge;
    private String url;
    private String info;
}
