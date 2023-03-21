package com.northernneckgarbage.nngc.dbConfig;

import com.northernneckgarbage.nngc.entity.StripeTransactions;
import com.northernneckgarbage.nngc.entity.dto.CustomerDTO;
import com.stripe.model.Account;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.JSONObject;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeApiResponse<T> {

    private String error;
    private StripeTransactions stripeTransactions;
    private T data;
    private Charge charge;
  private Account account;
private CustomerDTO customerDTO;

private String stripeCustomer;
    private String message;
}
