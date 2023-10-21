package com.northernneckgarbage.nngc.entity;

import com.northernneckgarbage.nngc.stripe.transaction.BillingDetails;
import com.northernneckgarbage.nngc.stripe.transaction.PaymentMethodDetails;
import com.stripe.model.Charge;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "stripe_transactions")
public class StripeTransactions {



    public enum Currency {
        USD, EUR
    }

    @Id
    @SequenceGenerator(name = "stripe_transactions_generator",
            sequenceName = "stripe_transactions_sequence", initialValue = 1)
    @GeneratedValue(generator = "stripe_transactions_generator")
    private Long id;

    private LocalDateTime createdAt;
    private String transactionId;
    private String description;
    private Long amount;
    private com.northernneckgarbage.nngc.entity.StripeTransactions.Currency currency = com.northernneckgarbage.nngc.entity.StripeTransactions.Currency.USD;
    private String stripeEmail;
    private String stripeToken;
    private String invoice;
    private String productID;
    private Boolean captured;
    private String status;
    private LocalDateTime expiresAt;
    private String invoiceURL;
    private String invoicePDF;
    private String receiptUrl;
    private boolean paid;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private Customer customer;

    String billingDetails;
    private String paymentMethodDetails;

    // Add any additional methods that were in either of the original classes

  //   For example, if you had a static method to create StripeTransactions from Charge
    public static StripeTransactions fromCharge(Charge charge) {
        StripeTransactions transaction = new StripeTransactions();
        transaction.setTransactionId(charge.getId());
        transaction.setDescription(charge.getDescription());
        transaction.setAmount(charge.getAmount());
        transaction.setCurrency(Currency.valueOf(charge.getCurrency()));
        transaction.setStripeEmail(charge.getReceiptEmail());
        transaction.setStripeToken(charge.getAuthorizationCode());
        transaction.setInvoice(charge.getReceiptUrl());
        // ... add any other fields you need to set

        return transaction;
    }
}
