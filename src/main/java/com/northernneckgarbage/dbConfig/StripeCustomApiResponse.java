package com.northernneckgarbage.dbConfig;

import com.northernneckgarbage.entity.StripeTransactions;
import com.northernneckgarbage.stripe.transaction.RequestParams;
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
    private List<StripeTransactions> data;
    private Boolean has_more;
    private String url;
    private RequestParams request_params;

    public StripeCustomApiResponse(List<StripeTransactions> stripeTransactions, String s) {
    }
}

