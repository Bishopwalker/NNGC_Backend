package com.northernneckgarbage.nngc.dbConfig;

import com.stripe.model.Address;
import com.stripe.model.Invoice;
import com.stripe.model.InvoiceLineItem;
import com.stripe.model.ShippingDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeInvoiceResponse {

    private String message;
    private String error;
    private String id;
    private String object;
    private String account_country;
    private String account_name;
    private List<String> account_tax_ids;
    private Long amount_due;
    private Long amount_paid;
    private Long amount_remaining;

    private String amount_shipping;
    private String application;
    private Long application_fee_amount;
    private Long attempt_count;
    private Boolean attempted;
    private Boolean auto_advance;
    private String automatic_tax;
    private String billing_reason;
    private String charge;
    private String collection_method;
    private Long created;
    private String currency;
    private String customer;
    private Address customer_address;
    private String customer_email;
    private String customer_name;
    private String customer_phone;
    private String customer_shipping;
    private String customer_tax_exempt;
    private String customer_tax_ids;
    private String default_payment;
    private String default_payment_method;
    private String default_source;
    private String default_tax_rates;
    private String description;
    private String discount;
    private String discounts;
    private Long due_date;
    private Long ending_balance;
    private String footer;
    private String from_invoice;
    private String hosted_invoice_url;
    private String invoice_pdf;
    private String last_finalization_error;
    private String latest_revision;
    private String lines;
    private Boolean livemode;
    private Map<String, String> metadata;
    private Long next_payment_attempt;
    private String number;
    private String on_behalf_of;
    private String payment_intent;
    private String payment_settings;
    private Boolean paid;
    private Boolean paid_out_of_band;
    private Long period_end;
    private Long period_start;
    private Long post_payment_credit_notes_amount;
    private Long pre_payment_credit_notes_amount;
    private String quote;
    private String receipt_number;
    private String rendering_options;
    private Long starting_balance;
    private String shipping_cost;
    private String shipping_details;
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
    private Long webhooks_delivered_at;




}
