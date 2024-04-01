package com.northernneckgarbage.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentDTO {
    private double amount_due;
    private double amount_paid;
    private double amount_remaining;

    private String billing_reason;
    private String charge;
    private String collection_method;
    private String currency;
    private String default_payment;
    private String default_payment_method;
    private String description;
    private String discount;
    private String discounts;
    private double starting_balance;
    private String  due_date;
    private String period_start;
    private String period_end;
    private String ending_balance;
    private double post_payment_credit_notes_amount;
    private double pre_payment_credit_notes_amount;

}
