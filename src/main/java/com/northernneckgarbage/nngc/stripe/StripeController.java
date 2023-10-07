package com.northernneckgarbage.nngc.stripe;


import com.northernneckgarbage.nngc.dbConfig.StripeApiResponse;
import com.northernneckgarbage.nngc.dbConfig.StripeInvoiceResponse;
import com.northernneckgarbage.nngc.dbConfig.StripeProductResponse;
import com.northernneckgarbage.nngc.dbConfig.StripeRegistrationResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.entity.StripeTransactions;
import com.northernneckgarbage.nngc.stripe.transaction.StripeCustomApiResponse;
import com.northernneckgarbage.nngc.token.TokenRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("auth/stripe/")
@RequiredArgsConstructor
public class StripeController {


    private final TokenRepository tokenRepository;
    private final StripeService stripeService;
    private final StripeProductService stripeProductService;
private final StripeInvoiceService stripeInvoiceService;



    @PostMapping("/transaction")
    public ResponseEntity<StripeApiResponse> saveTransaction(@RequestBody StripeTransactions transactions){
        return ResponseEntity.ok(stripeService.addStripeTransaction(transactions));
    }

    @PostMapping("/transaction/{id}")
    public ResponseEntity<StripeApiResponse> addTransaction(@PathVariable Long id,
                                                               @RequestBody StripeTransactions transactions){
        return ResponseEntity.ok(stripeService.updateStripeCustomerTransaction(id, transactions));
    }

    @PostMapping("/stripe_id/{id}")
    public ResponseEntity<StripeRegistrationResponse> addStripeId(@PathVariable Long id,
                                                            @RequestBody String stripeId){
        log.info("stripeId: " + stripeId.split(":")[1]);
        return ResponseEntity.ok(stripeService.addStripeId(id, stripeId.split(":")[1]));
    }

    @PutMapping("/transaction/{id}")
    public ResponseEntity<StripeApiResponse> updateTransaction(@PathVariable Long id,
                                                               @RequestBody StripeTransactions transactions){
        return ResponseEntity.ok(stripeService.updateStripeCustomerTransaction(id, transactions));
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<StripeApiResponse> getStripeAccount(@PathVariable Long id) throws StripeException {
        return ResponseEntity.ok(stripeService.getStripeCustomer(id));
    }
    //Get Residential_TR
    @GetMapping("/product/res-trash-sub")
    public ResponseEntity<StripeProductResponse> getResTrashSub() throws StripeException {
        return ResponseEntity.ok(stripeProductService.residential_trash_sub());
    }

    //Get Dumpster
    @GetMapping("/product/dumpster")
    public ResponseEntity<StripeProductResponse> getDumpster() throws StripeException {
        return ResponseEntity.ok(stripeProductService.dumpster());
    }

    //Get Residential_TOnce
    @GetMapping("/product/res-trash-once")
    public ResponseEntity<StripeProductResponse> getResTrashOnce() throws StripeException {
        return ResponseEntity.ok(stripeProductService.residential_TOnce());
    }

    //Get all transactions
    @GetMapping("/transaction")
    public  ResponseEntity<Page<StripeTransactions>>  getAllTransactions(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(stripeService.getAllStripeTransactions(page, size));
    }

    //Get all transactions by customer id
    @GetMapping("/transaction/{id}")
    public ResponseEntity<Page<StripeTransactions>>  getAllTransactionsByCustomerID(@RequestHeader("Authorization")
                                                                                        String headers, @PathVariable Long id,
                                                                                    @RequestParam(defaultValue = "0")
                                                                                    int page, @RequestParam(defaultValue = "10") int size) {
       log.info("headers: " + headers);
        var user=tokenRepository.findByToken(headers).get().getCustomer().getAppUserRoles();
        var expired=tokenRepository.findByToken(headers).get().getExpiresAt().isBefore(LocalDateTime.now());
log.warn("expired: " + expired);
         if( expired & user.toString().equals("STRIPE_CUSTOMER") || user.toString().equals("ADMIN")){
            return ResponseEntity.ok(stripeService.getAllStripeTransactionsByCustomerId(id, page, size));
        }

        return ResponseEntity.ok().body((Page<StripeTransactions>) StripeApiResponse.builder().message("You are not authorized to view this page").build());
    }


    @GetMapping("/create-checkout-session_wid/res_trash_once/{id}")
    public ResponseEntity<StripeApiResponse> checkoutSession(@PathVariable long id ) throws StripeException {
        // Logic to create a Stripe session
        Session session = stripeService.createSessionForTrashOnceWID(id );
        return ResponseEntity.ok(StripeApiResponse.builder()
                .message(session.getUrl())
                .build());
    }
//create a get mapping that will take the long customerID and string productID in the body and return a session
    @GetMapping("/create-checkout-session/{id}")
     public  StripeApiResponse<ResponseEntity<StripeTransactions>> checkoutProduct(@PathVariable long id , @RequestParam String productID) throws StripeException {
        // Logic to create a Stripe session
        log.info( productID);
        Session session = stripeService.checkoutProduct(id, productID);
        return StripeApiResponse.<ResponseEntity<StripeTransactions>>builder()
                .message(session.getUrl())
                .build();
    }

    //Create Stripe Customer by customer id
    @GetMapping("/create-customer/{id}")
    public ResponseEntity<StripeApiResponse<com.stripe.model.Customer>> createStripeCustomer(@PathVariable Long id) throws StripeException {
        // Logic to create a Stripe session
  return ResponseEntity.ok(stripeService.createStripeCustomer(id));
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<StripeRegistrationResponse<Customer>> updateStripeCustomerID(@PathVariable Long id,
                                                                                       @RequestBody StripeTransactions customerID)
            {
        return ResponseEntity.ok(stripeService.addStripeTransaction2Customer(id, customerID));
    }


    @GetMapping("/create-checkout-session_wid/res_sub/{id}")
    public String checkoutSessionSub(@PathVariable long id) throws StripeException {
        // Logic to create a Stripe session
        Session session = stripeService.createSessionForTrashSubscriptionWID(id);
        return "redirect: " + session.getUrl();
    }




    @GetMapping("/create-checkout-session_wid/dumpster/{id}")
    public String checkoutSessionDumpsterID(@PathVariable long id) throws StripeException {
        // Logic to create a Stripe session
        Session session = stripeService.createSessionForDumpsterWID(id);
        return "redirect: " + session.getUrl();
    }

    //Get Request to retrive stripe Product by id
    @GetMapping("/products/{id}")
    public ResponseEntity<StripeProductResponse> getStripeProduct(@PathVariable String id) throws StripeException {
        return ResponseEntity.ok(stripeProductService.retrieveProduct(id) );
    }


//Get Request to create invoice
    @GetMapping("/create-invoice/{id}")
    public ResponseEntity<StripeInvoiceResponse> createInvoice(@PathVariable Long id) throws Exception {

        return ResponseEntity.ok(stripeInvoiceService.createInvoice(id) );
    }
    @ExceptionHandler(StripeException.class)
    public ResponseEntity<StripeApiResponse> handleStripeException(StripeException ex) {
        return ResponseEntity.badRequest().body(StripeApiResponse.builder()
                .error(ex.getMessage())
                .build());
    }

    @GetMapping("/create-customer-portal-session/{id}")
    public ResponseEntity<String> createCustomerPortalSession(@PathVariable Long id) {
        try {
            String sessionUrl = stripeInvoiceService.createCustomerPortalSession(id);
            return ResponseEntity.ok(sessionUrl);
        } catch (StripeException e) {
           log.error("An error occurred while creating the customer portal session.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the customer portal session.");
        }
    }

    @GetMapping("/all-products")
    public ResponseEntity<StripeProductResponse> getAllProducts() throws StripeException {
        return ResponseEntity.ok((StripeProductResponse) stripeProductService.getAllProducts());
    }

    @GetMapping("/all-invoices/{id}")
    public ResponseEntity<StripeCustomApiResponse> getAllInvoicesByID(@PathVariable Long id) throws StripeException {
        return ResponseEntity.ok(stripeService.getAllTransactionsFromStripeByCustomerId(id));
    }
}
