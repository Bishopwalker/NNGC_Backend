package com.northernneckgarbage.nngc.dbConfig;

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
public class StripeApiResponse<T> {

    private String error;
    private StripeTransactions stripeTransactions;
    private T data;
    private Charge charge;
    private String url;
    private String info;
    private String message;
}
