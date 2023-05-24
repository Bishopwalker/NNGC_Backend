package com.northernneckgarbage.nngc.stripe.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeCustomApiResponse {
    private List<StripeTransaction> data;
    private Boolean has_more;
    private String url;
    private RequestParams request_params;

    public StripeCustomApiResponse(List<StripeTransaction> stripeTransactions, String s) {
    }
}
