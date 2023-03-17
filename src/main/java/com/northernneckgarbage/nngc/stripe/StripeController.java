package com.northernneckgarbage.nngc.stripe;


import com.northernneckgarbage.nngc.dbConfig.ApiResponse;
import com.northernneckgarbage.nngc.dbConfig.StripeApiResponse;
import com.northernneckgarbage.nngc.dbConfig.StripeRegistrationResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.entity.StripeTransactions;
import com.northernneckgarbage.nngc.token.TokenRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.checkout.Session;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("auth/stripe/")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
@RequiredArgsConstructor
public class StripeController {


    private final TokenRepository tokenRepository;
    private final StripeService stripeService;




    @PostMapping("/transaction")
    public ResponseEntity<StripeApiResponse> saveTransaction(@RequestBody StripeTransactions transactions){
        return ResponseEntity.ok(stripeService.addStripeTransaction(transactions));
    }

    @PostMapping("/transaction/{id}")
    public ResponseEntity<StripeApiResponse> addTransaction(@PathVariable Long id,
                                                               @RequestBody StripeTransactions transactions){
        return ResponseEntity.ok(stripeService.updateStripeCustomerTransaction(id, transactions));
    }

    @PutMapping("/transaction/{id}")
    public ResponseEntity<StripeApiResponse> updateTransaction(@PathVariable Long id,
                                                               @RequestBody StripeTransactions transactions){
        return ResponseEntity.ok(stripeService.updateStripeCustomerTransaction(id, transactions));
    }

    //Get all transactions
    @GetMapping("/transaction")
    public  ResponseEntity<Page<StripeTransactions>>  getAllTransactions(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(stripeService.getAllStripeTransactions(page, size));
    }

    //Get all transactions by customer id
    @GetMapping("/transaction/{id}")
    public ResponseEntity<Page<StripeTransactions>>  getAllTransactionsByCustomerID(@RequestHeader("Authorization") String headers, @PathVariable Long id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
       log.info("headers: " + headers);
        var user=tokenRepository.findByToken(headers).get().getCustomer().getAppUserRoles();
        log.info(user.toString());
        if(user.toString().equals("STRIPE_CUSTOMER") || user.toString().equals("ADMIN")){
            return ResponseEntity.ok(stripeService.getAllStripeTransactionsByCustomerId(id, page, size));
        }

        return ResponseEntity.badRequest().body((Page<StripeTransactions>) StripeApiResponse.builder().message("You are not authorized to view this page").build());
    }

    @GetMapping("/create-checkout-session/res_trash_once")
    public ResponseEntity<StripeApiResponse> checkoutSession() throws StripeException {
        // Logic to create a Stripe session
        Session session = stripeService.createSessionForTrashOnce();
        return ResponseEntity.ok(StripeApiResponse.builder()
                .url(session.getUrl())
                .info(session.getBillingAddressCollection())
                .build());
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<StripeRegistrationResponse<Customer>> updateStripeCustomerID(@PathVariable Long id,
                                                                                       @RequestBody StripeTransactions customerID)
            {
        return ResponseEntity.ok(stripeService.addStripeCustomerID(id, customerID));
    }

    @GetMapping("/create-checkout-session/res_trash_Sub")
    public String checkoutSessionSub() throws StripeException {
        // Logic to create a Stripe session
        Session session = stripeService.createSessionForTrashSubscription();
        return "redirect: " + session.getUrl();
    }


    @GetMapping("/create-checkout-session/dumpster")
    public String checkoutSessionDumpster() throws StripeException {
        // Logic to create a Stripe session
        Session session = stripeService.createSessionForDumpster();
        return "redirect: " + session.getUrl();
    }

    @ExceptionHandler(StripeException.class)
    public ResponseEntity<StripeApiResponse> handleStripeException(StripeException ex) {
        return ResponseEntity.badRequest().body(StripeApiResponse.builder()
                .error(ex.getMessage())
                .build());
    }

    public ResponseEntity<StripeApiResponse> charge(@RequestBody StripeTransactions payment) throws StripeException {
        return ResponseEntity.ok(StripeApiResponse.builder()
                .stripeTransactions(payment)
                .build());
    }
}
