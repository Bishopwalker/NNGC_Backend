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
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "stripe_transactions")
public class StripeTransactions {

    public StripeTransactions(String id, int intExact, String currency, String description, Boolean captured, String status, BillingDetails billingDetails, PaymentMethodDetails paymentMethodDetails) {

    }

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
    private Currency currency = Currency.USD;
    private String stripeEmail;
    private String stripeToken;
    private String invoice;
    private String productID;
    private Boolean captured;
    private String status;
    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private Customer customer;
String  billingDetails;
    private String paymentMethodDetails;

    // Add any additional methods that were in either of the original classes

    // For example, if you had a static method to create StripeTransactions from Charge
    public static StripeTransactions fromCharge(Charge charge) {
        StripeTransactions transaction = new StripeTransactions();
        transaction.setTransactionId(charge.getId());
        transaction.setDescription(charge.getDescription());
        transaction.setAmount(charge.getAmount());
        transaction.setCurrency(Currency.valueOf(charge.getCurrency()));
        transaction.setStripeEmail(charge.getReceiptEmail());
        transaction.setStripeToken(charge.getAuthorizationCode());
        transaction.setInvoice(charge.getInvoice());
        // ... add any other fields you need to set

        return transaction;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        StripeTransactions that = (StripeTransactions) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
