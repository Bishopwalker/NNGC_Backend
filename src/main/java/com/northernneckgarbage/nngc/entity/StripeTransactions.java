package com.northernneckgarbage.nngc.entity;

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
    private int amount;
    private Currency currency;
    private String stripeEmail;
    private String stripeToken;
    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private Customer customer;

}
