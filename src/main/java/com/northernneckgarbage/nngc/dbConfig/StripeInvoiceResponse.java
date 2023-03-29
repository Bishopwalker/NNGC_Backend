package com.northernneckgarbage.nngc.dbConfig;

import com.northernneckgarbage.nngc.entity.dto.CustomerDTO;
import com.northernneckgarbage.nngc.entity.dto.PaymentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeInvoiceResponse {

    private String message;
private CustomerDTO app_customer;
    private String invoiceID;
    private String object;
    private String account_country;
    private PaymentDTO app_payment;
    private String account_name;
    private List<String> account_tax_ids;
;

    private String amount_shipping;


    private Long attempt_count;
    private Boolean attempted;


    private String createdAt;
    private String currency;

    private String customer_tax_exempt;

    private String default_tax_rates;


    private Long ending_balance;
    private String footer;

    private String hosted_invoice_url;
    private String invoice_pdf;
    private String last_finalization_error;
    private String latest_revision;
    private String lines;
    private Boolean livemode;
    private Map<String, String> metadata;
    private Long next_payment_attempt;
    private String number;

    private String payment_intent;
    private String payment_settings;
    private Boolean paid;
    private Boolean paid_out_of_band;


    private String quote;
    private String receipt_number;



    private Long statement_descriptor;
    private String status;
    private String status_transitions;

    private String subscription;
    private Long subtotal;
    private Long subtotal_excluding_tax;
    private Long tax;
    private String test_clock;
    private Long total;
    private String total_discount_amounts;
    private Long total_excluding_tax;
    private String total_tax_amounts;
    private String transfer_data;
    private String webhooks_delivered_at;

    private String error;



}
