package com.northernneckgarbage.nngc.stripe;

import com.northernneckgarbage.nngc.controller.SseController;
import com.northernneckgarbage.nngc.dbConfig.StripeApiResponse;
import com.northernneckgarbage.nngc.dbConfig.StripeCustomApiResponse;
import com.northernneckgarbage.nngc.dbConfig.StripeRegistrationResponse;
import com.northernneckgarbage.nngc.entity.StripeTransactions;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.northernneckgarbage.nngc.roles.AppUserRoles;
import com.northernneckgarbage.nngc.stripe.transaction.Address;
import com.northernneckgarbage.nngc.stripe.transaction.Card;
import com.northernneckgarbage.nngc.stripe.transaction.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.ChargeListParams;
import com.stripe.param.checkout.SessionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StripeService {


Dotenv dotenv = Dotenv.load();

private final CustomerRepository customerRepository;
private final StripeTransactionRepository stripeTransactionRepository;
    private final SseController sseController;

    public static LocalDateTime convertMillisToLocalDateTime(long millis) {
        // Convert milliseconds to Duration
        Duration duration = Duration.ofMillis(millis);

        // Extract hours, minutes, and seconds from the duration
        long hours = duration.toHours();
        long minutes = duration.toMinutes() - (hours * 60);
        long seconds = duration.getSeconds() - (hours * 3600 + minutes * 60);

        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Create a LocalTime object using the hours, minutes, and seconds
        LocalTime extractedTime = LocalTime.of((int) hours, (int) minutes, (int) seconds);

        // Combine currentDate and extractedTime to create LocalDateTime
        return LocalDateTime.of(currentDate, extractedTime);
    }
public StripeService(CustomerRepository customerRepository, StripeTransactionRepository stripeTransactionRepository, SseController sseController) throws StripeException {
    this.sseController = sseController;

    Stripe.apiKey = dotenv.get("STRIPE_SECRET_KEY");

    Stripe.setAppInfo(
            "NNGC-Server",
            "0.0.2",
            "http://localhost:8080"
    );
    this.customerRepository = customerRepository;
    this.stripeTransactionRepository = stripeTransactionRepository;


}
    public void handleEvent(Event event) {
        StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);

        if (stripeObject == null) {
            // Handle deserialization failure, e.g., log an error or throw an exception
            return;
        }

        switch (event.getType()) {
            case "payment_intent.succeeded", "payment_intent.created","charge.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                // Then define and call a method to handle the successful payment intent.
                // handlePaymentIntentSucceeded(paymentIntent);
                sseController.sendEventToClients("payment.succeeded");
                sseController.sendEventToClients("charge.succeeded");

                log.info("Payment Intent: " + paymentIntent);
                var updateTransaction = StripeTransactions.builder()
                        .amount((long) Math.toIntExact(paymentIntent.getAmount()))
                        .currency(StripeTransactions.Currency.USD)
                        .description(paymentIntent.getDescription())
                        .stripeToken(paymentIntent.getId())
                        .stripeEmail(paymentIntent.getReceiptEmail())
                        .transactionId(paymentIntent.getPaymentMethod())
                        .build();
                log.info("updateTransaction: {}", updateTransaction);

                break;
            case "payment_method.attached":
                PaymentMethod paymentMethod = (PaymentMethod) stripeObject;
                // Then define and call a method to handle the successful attachment of a PaymentMethod.
                // handlePaymentMethodAttached(paymentMethod);
              case"checkout.session.completed":
                  assert stripeObject instanceof Session;
                  Session session = (Session) stripeObject;
                  sseController.sendEventToClients("checkout.session.completed");
                  log.info("Session: " + session);
                  var email =session.getCustomerEmail();
                  Optional<com.northernneckgarbage.nngc.entity.Customer> customer = Optional.ofNullable(customerRepository.findByEmail(email).orElseThrow(() ->
                          new RuntimeException("Customer not found")));

                  var trans = StripeTransactions.builder()
                          .billingDetails(session.getBillingAddressCollection())
                          .amount(session.getAmountTotal())
                          .transactionId(session.getId())
                          .stripeToken(session.getClientReferenceId())
                          .createdAt(convertMillisToLocalDateTime(session.getCreated()))
                          .expiresAt(convertMillisToLocalDateTime(session.getExpiresAt()))
                          .stripeEmail(email)
                          .description(session.getInvoiceObject().getDescription())
                          .invoice(session.getInvoice())
                          .status(session.getPaymentStatus())
                          .currency(StripeTransactions.Currency.valueOf(session.getCurrency()))
                          .build();

             var id = customer.get().getId();
                  updateStripeCustomerTransaction(id,trans);
                  break;

              case "invoice.payment_succeeded":
                  assert stripeObject instanceof Invoice;
                  Invoice invoice = (Invoice) stripeObject;
                  log.info("Invoice: " + invoice);
                break;
                case "invoice.payment_failed", "invoice.finalized","invoice.finalization_failed":
                    Invoice invoice1 = (Invoice) stripeObject;
                    log.info("Invoice: " + invoice1);
                    break;
            case "customer.source.created", "customer.updated":
                Customer customer1 = (Customer) stripeObject;
                // Then define and call a method to handle the successful attachment of a PaymentMethod.
                // handlePaymentMethodAttached(paymentMethod);
                log.info("Customer: " + customer1);
                break;
            // Then define and call a method to handle the successful attachment of a PaymentMethod.
                // handlePaymentMethodAttached(paymentMethod);


            // ... handle other event types
            default:
                System.out.println("Unhandled event type: " + event.getType());
        }
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
    public void createStripeCustomersForAllUsers() {
        // Get all users
        List<com.northernneckgarbage.nngc.entity.Customer> users = customerRepository.findAll();

        // Loop through each user
        for (com.northernneckgarbage.nngc.entity.Customer user : users) {
            // Check if user has a Stripe ID
            if (user.getStripeCustomerId() == null) {
                // If not, create a Stripe customer for that user
                try {
                    createStripeCustomer(user.getId());
                } catch (StripeException e) {
                    // Handle the exception (you may want to log the error and continue with the next user)
                    System.err.println("Failed to create a Stripe customer for user " + user.getId() + ": " + e.getMessage());
                }
            }
        }
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

    String YOUR_DOMAIN = "http://localhost:5173/";//

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
                .amount((long) Math.toIntExact(price.getUnitAmount())) // Set the transaction amount using the price object
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
    public Session checkoutProduct(long id, String productId) throws StripeException {

        var user = customerRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Customer not found"));
        if(user.getStripeCustomerId() == null){
            createStripeCustomer(id);
            user = customerRepository.findById(id).orElseThrow(() ->
                    new RuntimeException("Customer not found"));
        }

       Product product = Product.retrieve(productId);
        log.info("Product: " +productId);
        log.info("Product: " +product);
        Price price = Price.retrieve(product.getDefaultPrice());
log.info("Price: " + price);
        SessionCreateParams.Mode mode =
                (price.getRecurring() != null && price.getRecurring().getIntervalCount() > 0)
                        ? SessionCreateParams.Mode.SUBSCRIPTION
                        : SessionCreateParams.Mode.PAYMENT;
        String successUrl = (mode == SessionCreateParams.Mode.SUBSCRIPTION)
                ? YOUR_DOMAIN + "dashboard"
                : YOUR_DOMAIN + "appointment";
        log.info("Success URL: " + successUrl);
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setCustomerEmail(user.getEmail())
                        .setMode(mode)
                        .setSuccessUrl( successUrl)
                        .setCancelUrl(YOUR_DOMAIN + "services")
                        .setAutomaticTax(
                                SessionCreateParams.AutomaticTax.builder()
                                        .setEnabled(true)
                                        .build())
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPrice(price.getId())
                                        .build())
                        .build();

        Session session = Session.create(params);

        StripeTransactions transactions = StripeTransactions.builder()
                .amount((long) Math.toIntExact(price.getUnitAmount())) // Set the transaction amount using the price object
                .currency(StripeTransactions.Currency.USD)
                .description(product.getDescription())
                .stripeToken(session.getId())
                .stripeEmail(user.getEmail())
                .transactionId(params.getClientReferenceId())
                .productID(productId)
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
        log.info("Price ID: " + priceId);
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
                .amount((long) Math.toIntExact(price.getUnitAmount())) // Set the transaction amount using the price object
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
                .amount((long) Math.toIntExact(price.getUnitAmount())) // Set the transaction amount using the price object
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

    public StripeCustomApiResponse getAllTransactionsFromStripeByCustomerId(Long id) throws StripeException {
        var user = customerRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Customer not found"));
        if(user.getStripeCustomerId() == null){
            createStripeCustomer(id);
            user = customerRepository.findById(id).orElseThrow(() ->
                    new RuntimeException("Customer not found"));
        }
        ChargeListParams listParams = ChargeListParams.builder().setCustomer(user.getStripeCustomerId()).build();
        ChargeCollection charges = Charge.list(listParams);
        log.info("Charges: " + charges);

        List<StripeTransactions> stripeTransactions = charges.getData().stream()
                .map(charge -> {
                    BillingDetails billingDetails = new BillingDetails(charge.getBillingDetails().getEmail(),
                            charge.getBillingDetails().getName(),
                            new Address(charge.getBillingDetails().getAddress().getCity(),
                                    charge.getBillingDetails().getAddress().getCountry(),
                                    charge.getBillingDetails().getAddress().getLine1(),
                                    charge.getBillingDetails().getAddress().getPostalCode(),
                                    charge.getBillingDetails().getAddress().getState()));

                    PaymentMethodDetails paymentMethodDetails = new PaymentMethodDetails(charge.getPaymentMethodDetails().getType(),
                            new Card(charge.getPaymentMethodDetails().getCard().getBrand(),
                                    charge.getPaymentMethodDetails().getCard().getCountry(),
                                    Math.toIntExact(charge.getPaymentMethodDetails().getCard().getExpMonth()),
                                    Math.toIntExact(charge.getPaymentMethodDetails().getCard().getExpYear()),
                                    charge.getPaymentMethodDetails().getCard().getLast4()));

                    return new StripeTransactions(charge.getId(), Math.toIntExact(charge.getAmount()), charge.getCurrency(),
                            charge.getDescription(), charge.getCaptured(), charge.getStatus(), billingDetails, paymentMethodDetails);
                }).collect(Collectors.toList());

        return new StripeCustomApiResponse(stripeTransactions, charges.getHasMore(), charges.getUrl(),
                new RequestParams(user.getStripeCustomerId()));
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
