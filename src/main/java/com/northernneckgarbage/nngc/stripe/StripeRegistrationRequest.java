package com.northernneckgarbage.nngc.stripe;

import com.northernneckgarbage.nngc.entity.dto.CustomerDTO;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class StripeRegistrationRequest {

    private final String email;
    private final String password;
    private final String stripeCustomerId;

}
