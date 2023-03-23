package com.northernneckgarbage.nngc.stripe;

import com.northernneckgarbage.nngc.dbConfig.StripeInvoiceResponse;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.stripe.Stripe;
import com.stripe.model.Invoice;
import com.stripe.model.InvoiceLineItem;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StripeInvoiceService {
    Dotenv dotenv = Dotenv.load();

    private CustomerRepository customerRepository;
    public StripeInvoiceService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
        Stripe.apiKey = dotenv.get("STRIPE_SECRET_KEY");
        Stripe.setAppInfo(
                "NNGC-Server",
                "0.0.2",
                "http://localhost:5000"
        );
    }


    public StripeInvoiceResponse createInvoice(Long customerId) throws Exception { var user = customerRepository.findById(customerId).orElseThrow(()-> new Exception("Customer not found" + customerId));
        Map<String, Object> params = new HashMap<>();
        params.put("customer", user.getStripeCustomerId());

        Invoice invoice = Invoice.create(params);

        //Construct the invoice response with the necessary key value attributes
        return StripeInvoiceResponse.builder()
                .message("Invoice Created")
                .id(invoice.getId())
                .object(invoice.getObject())
                .account_country(invoice.getAccountCountry())
                .account_name(invoice.getAccountName())
                .account_tax_ids(invoice.getAccountTaxIds())
                .amount_due(invoice.getAmountDue())
                .amount_paid(invoice.getAmountPaid())
                .amount_remaining(invoice.getAmountRemaining())
                .amount_shipping(String.valueOf(invoice.getCustomerShipping()))
                .application(invoice.getApplication())
                .application_fee_amount(invoice.getApplicationFeeAmount())
                .attempt_count(invoice.getAttemptCount())
                .attempted(invoice.getAttempted())
                .auto_advance(invoice.getAutoAdvance())
                .automatic_tax(String.valueOf(invoice.getAutomaticTax()))
                .billing_reason(invoice.getBillingReason())
                .charge(invoice.getCharge())
                .collection_method(invoice.getCollectionMethod())
                .created(invoice.getCreated())
                .currency(invoice.getCurrency())
                .customer(invoice.getCustomer())
                .customer_address(invoice.getCustomerAddress())
                .customer_email(invoice.getCustomerEmail())
                .customer_name(invoice.getCustomerName())
                .customer_phone(invoice.getCustomerPhone())
                .customer_shipping(String.valueOf(invoice.getCustomerShipping()))
                .customer_tax_exempt(invoice.getCustomerTaxExempt())
                .customer_tax_ids(invoice.getCustomerTaxIds().toString())
                .default_payment_method(invoice.getDefaultPaymentMethod())
                .default_source(invoice.getDefaultSource())
                .default_tax_rates(invoice.getDefaultTaxRates().toString())
                .description(invoice.getDescription())
                .discount(String.valueOf(invoice.getDiscount()))
                .discounts(invoice.getDiscounts().toString())
                .due_date( invoice.getDueDate())
                .ending_balance(invoice.getEndingBalance())
                .footer(invoice.getFooter())
                .from_invoice(String.valueOf(invoice.getFromInvoice()))
                .hosted_invoice_url(invoice.getHostedInvoiceUrl())
                .invoice_pdf(invoice.getInvoicePdf())
                .last_finalization_error(String.valueOf(invoice.getLastFinalizationError()))
                .latest_revision(invoice.getLatestRevision())
                .lines(String.valueOf(invoice.getLines()))
                .livemode(invoice.getLivemode())
                .metadata(invoice.getMetadata())
                .next_payment_attempt(invoice.getNextPaymentAttempt())
                .number(invoice.getNumber())
                .on_behalf_of(invoice.getOnBehalfOf())
                .paid(invoice.getPaid())
                .paid_out_of_band(invoice.getPaidOutOfBand())
                .payment_intent(invoice.getPaymentIntent())
                .payment_settings(String.valueOf(invoice.getPaymentSettings()))
                .period_end(invoice.getPeriodEnd())
                .period_start(invoice.getPeriodStart())
                .post_payment_credit_notes_amount(invoice.getPostPaymentCreditNotesAmount())
                .pre_payment_credit_notes_amount(invoice.getPrePaymentCreditNotesAmount())
                .quote(invoice.getQuote())
                .receipt_number(invoice.getReceiptNumber())
                .rendering_options(String.valueOf(invoice.getRenderingOptions()))
                .shipping_cost(String.valueOf(invoice.getCustomerShipping()))
                .shipping_details(String.valueOf(invoice.getCustomerShipping()))
                .starting_balance(invoice.getStartingBalance())

                .status(invoice.getStatus())

                .subscription(invoice.getSubscription())
                .subtotal(invoice.getSubtotal())
                .subtotal_excluding_tax(invoice.getSubtotalExcludingTax())
                .tax(invoice.getTax())
                .test_clock(invoice.getTestClock())
                .total(invoice.getTotal())
                .total_discount_amounts(invoice.getTotalDiscountAmounts().toString())
                .total_excluding_tax(invoice.getTotalExcludingTax())
                .total_tax_amounts(invoice.getTotalTaxAmounts().toString())
                .transfer_data(String.valueOf(invoice.getTransferData()))
                .webhooks_delivered_at(invoice.getWebhooksDeliveredAt())
                .build();
    }


}
