package com.northernneckgarbage.nngc.stripe;

import com.northernneckgarbage.nngc.controller.SseController;
import com.northernneckgarbage.nngc.dbConfig.StripeApiResponse;
import com.northernneckgarbage.nngc.dbConfig.StripeCustomApiResponse;
import com.northernneckgarbage.nngc.dbConfig.StripeRegistrationResponse;
import com.northernneckgarbage.nngc.entity.StripeTransactions;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.northernneckgarbage.nngc.repository.StripeTransactionRepository;
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
import jakarta.persistence.NoResultException;
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
public StripeService(CustomerRepository customerRepository, StripeTransactionRepository stripeTransactionRepository, SseController sseController) {
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
            case "customer.created", "customer.source.created", "customer.updated":
                Customer newCustomer = (Customer) stripeObject;
              log.info("Customer: " + newCustomer);
                // Then define and call a method to handle the successful attachment of a PaymentMethod.
                //handlePaymentMethodAttached(paymentMethod);
               // log.info("Customer: " + newCustomer);
                break;
            case"product.created":
                Product product = (Product) stripeObject;
              //  log.info("Product: " + product);
                break;
            case "plan.created":
                Plan plan = (Plan) stripeObject;
              //  log.info("Plan: " + plan);
                break;
            case "price.created"
                    , "price.updated"
                    , "price.deleted":
                Price price = (Price) stripeObject;
                //log.info("Price: " + price);
                break;

                case "charge.succeeded":
                Charge charge = (Charge) stripeObject;
                log.info("Charge: " + charge.getReceiptUrl());
              log.info("Charge: " + charge.getBillingDetails().getEmail());
                var url = charge.getReceiptUrl();
                var email = charge.getBillingDetails().getEmail();
//
                var customer = Optional.ofNullable(customerRepository.findByEmail(email).orElseThrow(() ->
                        new RuntimeException("Customer not found")));
              customer.ifPresent(c -> c.setReceiptURL(url));
try {
    customerRepository.saveAndFlush(customer.get());
}catch (NoResultException e){
    log.info("Customer already exists");
}catch (Exception e){
    log.error("Error: " + e.getMessage());
}
         // log.info("Customer: " + customer);

//
//                    log.info(updateTransaction.toString());
//                    stripeTransactionRepository.saveAndFlush(updateTransaction);
                   // stripeTransactionRepository.save(updateTransaction);
                  //  addStripeTransaction(updateTransaction);
                    sseController.sendEventToClients( url);
                break;

            case "payment_intent.succeeded", "payment_intent.created":
                assert stripeObject instanceof PaymentIntent;
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
          //  log.info("Payment Intent: " + paymentIntent);
             //  sseController.sendEventToClients( paymentIntent) ;

                break;
            case "customer.subscription.created":
                Subscription subscription = (Subscription) stripeObject;
                // Then define and call a method to handle the successful attachment of a PaymentMethod.

            log.info("Subscription: " + subscription);
                sseController.sendEventToClients("customer.subscription.created" + subscription);
                break;
            case "payment_method.attached":
                PaymentMethod paymentMethod = (PaymentMethod) stripeObject;
                // Then define and call a method to handle the successful attachment of a PaymentMethod.
              //  sseController.sendEventToClients( paymentMethod);
                break;
            case "checkout.session.completed":
                assert stripeObject instanceof Session;
                Session session = (Session) stripeObject;
 //    log.info("Session: " + session);
                var email1 = session.getCustomerDetails().getEmail();
               log.info("Email: " + email1);
//                var customerSession = Optional.ofNullable(customerRepository.findByEmail(email1).orElseThrow(() ->
//                        new RuntimeException("Customer not found")));
//               log.info("Customer: " + customerSession);
//

              //  sseController.sendEventToClients(session);

               break;
            case "invoice.created","invoice.upcoming":
                assert stripeObject instanceof Invoice;
                Invoice invoice2 = (Invoice) stripeObject;
      // log.info("Invoice: " + invoice2);



               // sseController.sendEventToClients("invoice.created");

                break;

            case "invoice.payment_succeeded","invoice.paid":
                assert stripeObject instanceof Invoice;
                Invoice invoice = (Invoice) stripeObject;
                log.info("{}",invoice);
          log.info("Invoice: " + invoice.getCustomer());

            var customerInvoice = Optional.ofNullable(customerRepository.locateByStripeID(invoice.getCustomer()).orElseThrow(() ->
                    new RuntimeException("Customer not found")));
    log.info("Customer: " + customerInvoice);
                customerInvoice.ifPresent(c -> c.setInvoiceURL(invoice.getHostedInvoiceUrl()));
               try {
                   customerRepository.saveAndFlush(customerInvoice.get());
               }catch (NoResultException e){
                   log.info("Customer already exists");
                }catch (Exception e){
                     log.error("Error: " + e.getMessage());
               }
                sseController.sendEventToClients( invoice);

                break;
                case "invoice.payment_failed","invoice.finalization_failed", "invoice.finalized", "invoice.updated":
                Invoice invoice1 = (Invoice) stripeObject;
             //  log.info("Invoice: " + invoice1);
               // sseController.sendEventToClients( invoice1);
                break;
                case "customer.subscription.trial_will_end":
                Subscription subscription1 = (Subscription) stripeObject;
                break;
                case "setup_intent.created","setup_intent.succeeded":
                SetupIntent setupIntent = (SetupIntent) stripeObject;
                log.info("Setup Intent: " + setupIntent);
                break;
                case "balance.available":
                Balance balance = (Balance) stripeObject;
                log.info("Balance: " + balance);
                break;
                case "payout.created","payout.reconciliation_completed","payout.paid":
                Payout payout = (Payout) stripeObject;
                log.info("Payout: " + payout);
                break;
            default:
                System.out.println("Unhandled event type: " + event.getType());
           //    sseController.sendEventToClients( event.getType());
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
                        .setCustomer(user.getStripeCustomerId())
                        .setMode(mode)
                        .setSuccessUrl( successUrl)
                        .setCancelUrl(YOUR_DOMAIN + "services")
//                        .setCustomerUpdate(  // Set customer_update parameters here
//                                SessionCreateParams.CustomerUpdate.builder()
//                                        .setAddress(SessionCreateParams.CustomerUpdate.Address.AUTO)  // Set address to 'auto'
//                                        .build()
//                        )
//                        .setAutomaticTax(
//                                SessionCreateParams.AutomaticTax.builder()
//                                        .setEnabled(true)
//                                        .build())
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPrice(price.getId())
                                        .build())

                        .build();

        //try {
//    StripeTransactions transactions = StripeTransactions.builder()
//            .amount((long) Math.toIntExact(price.getUnitAmount())) // Set the transaction amount using the price object
//            .currency(StripeTransactions.Currency.USD)
//            .description(product.getDescription())
//            .stripeToken(session.getId())
//            .stripeEmail(user.getEmail())
//            .transactionId(params.getClientReferenceId())
//            .productID(productId)
//            .customer(user)
//            .build();
//
//    updateStripeCustomerTransaction(id, transactions);
//}catch (Exception e){
//    log.error("Error: " + e.getMessage());
//}

        // Call the updateStripeCustomerTransaction method to update the transaction


        return Session.create(params);
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

        com.northernneckgarbage.nngc.entity.Customer finalUser = user;
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

                    return StripeTransactions.builder()
                            .amount((long) Math.toIntExact(charge.getAmount()))
                            .currency(StripeTransactions.Currency.USD)
                            .description(charge.getDescription())
                            .stripeToken(charge.getId())
                            .stripeEmail(charge.getBillingDetails().getEmail())
                            .transactionId(charge.getPaymentMethod())
                            .customer(finalUser)
                            .billingDetails(String.valueOf(billingDetails))
                            .paymentMethodDetails(String.valueOf(paymentMethodDetails))
                            .build();
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
