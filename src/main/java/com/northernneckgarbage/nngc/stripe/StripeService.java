package com.northernneckgarbage.nngc.stripe;

import com.stripe.model.Price;

import com.northernneckgarbage.nngc.dbConfig.StripeApiResponse;
import com.northernneckgarbage.nngc.dbConfig.StripeRegistrationResponse;
import com.northernneckgarbage.nngc.entity.StripeTransactions;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.northernneckgarbage.nngc.roles.AppUserRoles;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StripeService {


Dotenv dotenv = Dotenv.load();

private final CustomerRepository customerRepository;
private final StripeTransactionRepository stripeTransactionRepository;
public StripeService(CustomerRepository customerRepository, StripeTransactionRepository stripeTransactionRepository) throws StripeException {

    Stripe.apiKey = dotenv.get("STRIPE_SECRET_KEY");
    log.info("Stripe Secret Key: {}", dotenv.get("STRIPE_SECRET_KEY"));
    Stripe.setAppInfo(
            "NNGC-Server",
            "0.0.2",
            "http://localhost:5000"
    );
    this.customerRepository = customerRepository;
    this.stripeTransactionRepository = stripeTransactionRepository;
    Map<String, Object> params = new HashMap<>();
    params.put("customer","cus_NU0gEq31snHVZx");


}
//get stripe account from customer stripeId
public StripeApiResponse<Customer> getStripeCustomer(Long id) throws StripeException {
    var user = customerRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (user.getStripeCustomerId() == null) {
        throw new UsernameNotFoundException("Stripe ID not found");
    }

    var stripeId = user.getStripeCustomerId();
    var stripeCustomer = Customer.retrieve(stripeId);

    return StripeApiResponse.<Customer>builder()
            .stripeCustomer(stripeCustomer.toJson())
            .message("Stripe Customer retrieved")
            .build();
}

 public StripeRegistrationResponse   addStripeId(Long id, String stripeId) {
     var user = customerRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));

     stripeId = stripeId.substring(1, stripeId.length() - 2);

     var sol = stripeId.split("\"")[0].trim();
     log.info("Stripe ID: {}", sol);
//        log.info("Split: {}", );
  user.setStripeCustomerId(sol);
     customerRepository.saveAndFlush(user);
     return StripeRegistrationResponse.builder()
             .customerDTO(user.toCustomerDTO())
             .message("Stripe ID added to user")
             .build();
 }


public StripeRegistrationResponse addStripeTransaction2Customer(Long id, StripeTransactions transaction) {
    LocalDateTime now = LocalDateTime.now();
    transaction.setCreatedAt(now);
    var user = customerRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    user.setStripeTransactions((List<StripeTransactions>) transaction);
    user.setAppUserRoles(AppUserRoles.STRIPE_CUSTOMER);
    customerRepository.save(user);
    addStripeTransaction(transaction);
    return StripeRegistrationResponse.builder()
            .customerDTO(user.toCustomerDTO())
            .message("Stripe Customer ID added to user")
            .build();
}

public StripeApiResponse<StripeTransactions> addStripeTransaction(StripeTransactions transactions){
    LocalDateTime now = LocalDateTime.now();
    transactions.setCreatedAt(now);
    var stripeTransaction = stripeTransactionRepository.save(transactions);
    return StripeApiResponse.<StripeTransactions>builder()
            .stripeTransactions(stripeTransaction)
            .message("Transaction added without a user")
            .build();
}
public StripeApiResponse<StripeTransactions> updateStripeCustomerTransaction(Long id, StripeTransactions transactions){
    var user = customerRepository.findById(id).orElseThrow(() ->
            new RuntimeException("Customer not found"));

    var updateTransaction = StripeTransactions.builder()
            .amount(transactions.getAmount())
            .currency(transactions.getCurrency())
            .description(transactions.getDescription())
            .stripeEmail(user.getEmail())
            .transactionId(user.getStripeCustomerId())
            .stripeToken(transactions.getStripeToken())
            .customer(user)
            .build();
    log.info("updateTransaction: {}", updateTransaction);
    stripeTransactionRepository.save(updateTransaction);
user.setAppUserRoles(AppUserRoles.STRIPE_CUSTOMER);
    customerRepository.save(user);
    return StripeApiResponse.<StripeTransactions>builder()
            .stripeTransactions(updateTransaction)
            .message("Stripe Transaction updated to user")
            .build();

}
//creat a function to update a stripe customer from the DB to the stripe account
//Using Stripe's API Customer Object not the DB Customer Object
    public StripeApiResponse<Customer> updateStripeCustomer(Long id) throws StripeException{
        var user = customerRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var stripeId = user.getStripeCustomerId();
        var stripeCustomer = Customer.retrieve(stripeId);
        stripeCustomer.update(Map.of(
                "email", user.getEmail(),
                "name", user.getFirstName() + " " + user.getLastName(),
                "description", "Customer for " + user.getEmail(),
                "phone", user.getPhone(),
                "address", Map.of(
                        "line1", user.getHouseNumber() + " " + user.getStreetName(),
                        "city",user.getCity(),
                        "state", user.getState(),
                        "postal_code", user.getZipCode(),
                        "country", "USA"
                )
        ));
        return StripeApiResponse.<Customer>builder()
                .customerDTO(user.toCustomerDTO())
                .message("Stripe Customer updated")
                .build();
    }

//Create a function to add a stripe customer from the DB to the stripe account
    //Using Stripe's API Customer Object not the DB Customer Object
    public StripeApiResponse<Customer> createStripeCustomer(Long id) throws StripeException {
        var user = customerRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var stripeCustomer = Customer.create(Map.of(
                "email", user.getEmail(),
                "name", user.getFirstName() + " " + user.getLastName(),
                "description", "Customer for " + user.getEmail(),
                "phone", user.getPhone(),
                "address", Map.of(
                        "line1", user.getHouseNumber() + " " + user.getStreetName(),
                        "city",user.getCity(),
                        "state", user.getState(),
                        "postal_code", user.getZipCode(),
                        "country", "USA"
                )
        ));
        user.setStripeCustomerId(stripeCustomer.getId());
        user.setAppUserRoles(AppUserRoles.STRIPE_CUSTOMER);
        customerRepository.save(user);
        return StripeApiResponse.<Customer>builder()
                .customerDTO(user.toCustomerDTO())
                .message("Stripe Customer created")
                .build();
    }

    public StripeApiResponse charge(StripeTransactions payment) throws StripeException {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", payment.getAmount());
        chargeParams.put("currency", payment.getCurrency());
        chargeParams.put("description", payment.getDescription());
        chargeParams.put("email", payment.getStripeEmail());
        chargeParams.put("source", dotenv.get("STRIPE_SECRET_KEY"));
        return StripeApiResponse.builder()
                .stripeTransactions(payment)
                .data(chargeParams)
                .charge(Charge.create(chargeParams))
                .build() ;
    }

    String YOUR_DOMAIN = "http://localhost:5173";


    public Session createSessionForTrashOnceWID(long id) throws StripeException {

        var user = customerRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Customer not found"));
        if(user.getStripeCustomerId() == null){
            createStripeCustomer(id);
            user = customerRepository.findById(id).orElseThrow(() ->
                    new RuntimeException("Customer not found"));
        }

        String priceId = "price_1MiksRACOG92rmQ4nQev74WZ";

        // Retrieve the price object from Stripe
        Price price = Price.retrieve(priceId);

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setCustomerEmail(user.getEmail())
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(YOUR_DOMAIN + "/")
                        .setCancelUrl(YOUR_DOMAIN + "?canceled=true")
                        .setAutomaticTax(
                                SessionCreateParams.AutomaticTax.builder()
                                        .setEnabled(true)
                                        .build())
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPrice(priceId)
                                        .build())
                        .build();

        Session session = Session.create(params);

        StripeTransactions transactions = StripeTransactions.builder()
                .amount(Math.toIntExact(price.getUnitAmount())) // Set the transaction amount using the price object
                .currency(StripeTransactions.Currency.USD)
                .description(price.getProduct())
                .stripeToken(session.getId())
                .stripeEmail(user.getEmail())
                .transactionId(params.getClientReferenceId())
                .customer(user)
                .build();

        // Call the updateStripeCustomerTransaction method to update the transaction
        updateStripeCustomerTransaction(id, transactions);

        return session;
    }


    public Session createSessionForTrashSubscriptionWID(long id) throws StripeException{
        var user = customerRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Customer not found"));
        if(user.getStripeCustomerId() == null){
            createStripeCustomer(id);
            user = customerRepository.findById(id).orElseThrow(() ->
                    new RuntimeException("Customer not found"));
        }

        String priceId = "price_1MiksRACOG92rmQ4YhSY0DOU";
        Price price = Price.retrieve(priceId);


                SessionCreateParams params =
                SessionCreateParams.builder()
                        .setCustomerEmail(user.getEmail())
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setSuccessUrl(YOUR_DOMAIN + "?success=true")
                        .setCancelUrl(YOUR_DOMAIN + "?canceled=true")
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        // Provide the exact Price ID (for example, pr_1234) of the product you want to sell
                                        .setPrice("price_1MiksRACOG92rmQ4YhSY0DOU")
                                        .build())
                        .build();

        Session session = Session.create(params);

        StripeTransactions transactions = StripeTransactions.builder()
                .amount(Math.toIntExact(price.getUnitAmount())) // Set the transaction amount using the price object
                .currency(StripeTransactions.Currency.USD)
                .description(price.getProduct())
                .stripeToken(session.getId())
                .stripeEmail(user.getEmail())
                .transactionId(params.getClientReferenceId())
                .customer(user)
                .build();

        // Call the updateStripeCustomerTransaction method to update the transaction
        updateStripeCustomerTransaction(id, transactions);

        return session;
    }


    public Session createSessionForDumpsterWID(long id) throws StripeException{
        var user = customerRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Customer not found"));
        if(user.getStripeCustomerId() == null){
            createStripeCustomer(id);
            user = customerRepository.findById(id).orElseThrow(() ->
                    new RuntimeException("Customer not found"));
        }

        String priceId = "price_1Mj1wXACOG92rmQ4eB6LWple";
        Price price = Price.retrieve(priceId);

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setCustomerEmail(user.getEmail())
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(YOUR_DOMAIN + "?success=true")
                        .setCancelUrl(YOUR_DOMAIN + "?canceled=true")
                        .setAutomaticTax(
                                SessionCreateParams.AutomaticTax.builder()
                                        .setEnabled(true)
                                        .build())
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        // Provide the exact Price ID (for example, pr_1234) of the product you want to sell
                                        .setPrice("price_1Mj1wXACOG92rmQ4eB6LWple")
                                        .build())
                        .build();

        Session session = Session.create(params);
        StripeTransactions transactions = StripeTransactions.builder()
                .amount(Math.toIntExact(price.getUnitAmount())) // Set the transaction amount using the price object
                .currency(StripeTransactions.Currency.USD)
                .description(price.getProduct())
                .stripeToken(session.getId())
                .stripeEmail(user.getEmail())
                .transactionId(params.getClientReferenceId())
                .customer(user)
                .build();

        // Call the updateStripeCustomerTransaction method to update the transaction
        updateStripeCustomerTransaction(id, transactions);

        return session;
    }

    //get all stripe_transactions with page
    public Page<StripeTransactions> getAllStripeTransactions(int amount, int size){
        Page<StripeTransactions> stripeTransactions = stripeTransactionRepository.findAll(PageRequest.of(amount, size));
        return stripeTransactions;
    }

    //get all stripe_transactions by customer id

    public Page<StripeTransactions> getAllStripeTransactionsByCustomerId(Long id, int amount, int size){
        Page<StripeTransactions> stripeTransactions = stripeTransactionRepository.findAllByCustomerId(id, PageRequest.of(amount, size));
        return stripeTransactions;
    }
}
