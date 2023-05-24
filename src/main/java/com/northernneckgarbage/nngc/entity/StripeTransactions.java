package com.northernneckgarbage.nngc.entity;

import com.stripe.model.Charge;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "stripe_transactions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StripeTransactions {



    public enum Currency {
        USD, EUR
    }
    @SequenceGenerator(name = "stripe_transactions_generator",
            sequenceName = "stripe_transactions_sequence", initialValue = 1)
    @GeneratedValue(generator = "stripe_transactions_generator")
    @Id
    private Long id;

    private LocalDateTime createdAt;


    private String transactionId;
    private String description;
    private Long amount;
    private Currency currency = Currency.USD;
    private String stripeEmail;
    private String stripeToken;
    public String invoice;
     @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private Customer customer;

    public static StripeTransactions fromCharge(Charge charge) {
        StripeTransactions transaction = new StripeTransactions();
        // set the fields of transaction using the data in charge
        transaction.setTransactionId(charge.getId());
        transaction.setDescription(charge.getDescription());
        transaction.setAmount(charge.getAmount());
        transaction.setCurrency(Currency.valueOf(charge.getCurrency()));
        transaction.setStripeEmail(charge.getReceiptEmail());
        transaction.setStripeToken(charge.getAuthorizationCode());
        transaction.setInvoice(charge.getInvoice());
        return transaction;
    }
}
