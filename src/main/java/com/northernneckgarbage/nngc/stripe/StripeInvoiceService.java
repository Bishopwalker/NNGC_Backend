package com.northernneckgarbage.nngc.stripe;

import com.northernneckgarbage.nngc.dbConfig.StripeInvoiceResponse;
import com.northernneckgarbage.nngc.entity.dto.AddressDTO;
import com.northernneckgarbage.nngc.entity.dto.CustomerDTO;
import com.northernneckgarbage.nngc.entity.dto.PaymentDTO;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Invoice;
import com.stripe.model.billingportal.Session;
import com.stripe.param.billingportal.SessionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class StripeInvoiceService {
    Dotenv dotenv = Dotenv.load();

    private final CustomerRepository customerRepository;

    public StripeInvoiceService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
        Stripe.apiKey = dotenv.get("STRIPE_SECRET_KEY");
        Stripe.setAppInfo(
                "NNGC-Server",
                "0.0.2",
                "https://3.85.8.238:8080"
        );
    }


    public StripeInvoiceResponse createInvoice(Long customerId) throws Exception { var user = customerRepository.findById(customerId).orElseThrow(()-> new Exception("Customer not found" + customerId));
        Map<String, Object> params = new HashMap<>();
        params.put("customer", user.getStripeCustomerId());

        Invoice invoice = Invoice.create(params);

        var address = AddressDTO.builder()
                             .line1(invoice.getCustomerAddress().getLine1())
                .line2(invoice.getCustomerAddress().getLine2())
                .city(invoice.getCustomerAddress().getCity())
                .state(invoice.getCustomerAddress().getState())
                .zipCode(invoice.getCustomerAddress().getPostalCode())
                .build();

var customer = CustomerDTO.builder()
        .id(user.getId())
        .fullName(invoice.getCustomerName())
        .email(invoice.getCustomerEmail())
        .phoneNumber(invoice.getCustomerPhone())
        .address(address)
        .stripeCustomerId(invoice.getCustomer())
        .build();

  var  createdAt = LocalDateTime.ofEpochSecond(invoice.getCreated(), 0, ZoneOffset.UTC);
 var periodStart = LocalDateTime.ofEpochSecond(invoice.getPeriodStart(), 0, ZoneOffset.UTC);
    var periodEnd = LocalDateTime.ofEpochSecond(invoice.getPeriodEnd(), 0, ZoneOffset.UTC);
   var webhooksDeliveredAt = LocalDateTime.ofEpochSecond(invoice.getWebhooksDeliveredAt(), 0, ZoneOffset.UTC);
var payment = PaymentDTO.builder()
        .amount_due(invoice.getAmountDue())
        .amount_paid(invoice.getAmountPaid())
        .amount_remaining(invoice.getAmountRemaining())
        .billing_reason(invoice.getBillingReason())
        .charge(invoice.getCharge())
        .currency(invoice.getCurrency())
        .default_payment(invoice.getDefaultPaymentMethod())
        .default_payment_method(invoice.getDefaultPaymentMethod())
        .description(invoice.getDescription())
        .discount(String.valueOf(invoice.getDiscount()))
        .discounts(invoice.getDiscounts().toString())
        .due_date(String.valueOf(invoice.getDueDate()))
        .period_start(String.valueOf(periodStart))
        .period_end(String.valueOf(periodEnd))
        .ending_balance(String.valueOf(invoice.getEndingBalance()))
        .starting_balance(invoice.getStartingBalance())
        .post_payment_credit_notes_amount(invoice.getPostPaymentCreditNotesAmount())
        .pre_payment_credit_notes_amount(invoice.getPrePaymentCreditNotesAmount())
        .build();


//convert milliseconds to a local date time

        //Construct the invoice response with the necessary key value attributes
        return StripeInvoiceResponse.builder()
                .message("Invoice Created")
                .invoiceID(invoice.getId())

                .app_customer(customer)
                .app_payment(payment)

                .account_country(invoice.getAccountCountry())
                .account_name(invoice.getAccountName())



                .attempt_count(invoice.getAttemptCount())
                .attempted(invoice.getAttempted())


                .createdAt(String.valueOf(createdAt))




               .footer(invoice.getFooter())

                .hosted_invoice_url(invoice.getHostedInvoiceUrl())
                .invoice_pdf(invoice.getInvoicePdf())
                .last_finalization_error(String.valueOf(invoice.getLastFinalizationError()))
                .latest_revision(invoice.getLatestRevision())
                .lines(String.valueOf(invoice.getLines().getUrl()))
                .livemode(invoice.getLivemode())
                .metadata(invoice.getMetadata())
                .next_payment_attempt(invoice.getNextPaymentAttempt())
                .number(invoice.getNumber())

                .paid(invoice.getPaid())
                .paid_out_of_band(invoice.getPaidOutOfBand())
                .payment_intent(invoice.getPaymentIntent())



                .quote(invoice.getQuote())
                .receipt_number(invoice.getReceiptNumber())




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
                .webhooks_delivered_at(String.valueOf(webhooksDeliveredAt))
                .build();
    }

    public String createCustomerPortalSession(Long customerId) throws StripeException {
        var user = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
        String stripeCustomerId = user.getStripeCustomerId();
// TODO: 10/7/2023 need to build a front end to signify billing completed
        SessionCreateParams params = SessionCreateParams.builder()
                .setCustomer(stripeCustomerId)
                .setReturnUrl("https://localhost:5173/")
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }
}
