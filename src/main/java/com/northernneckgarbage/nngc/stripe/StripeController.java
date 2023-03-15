package com.northernneckgarbage.nngc.stripe;


import com.northernneckgarbage.nngc.dbConfig.StripeApiResponse;
import com.northernneckgarbage.nngc.dbConfig.StripeRegistrationResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.checkout.Session;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("auth/stripe/")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
@RequiredArgsConstructor
public class StripeController {



    private final StripeService stripeService;



//    @GetMapping("/create-checkout-session/res_trash_once")
//    public String checkoutSession() throws StripeException {
//        // Logic to create a Stripe session
//        Session session = stripeService.createSessionForTrashOnce();
//        return "redirect: " + session.getUrl();
//    }

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
                                                                                       @RequestBody String customerID)
            throws StripeException {
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

    public ResponseEntity<StripeApiResponse> charge(@RequestBody StripePayment payment) throws StripeException {
        return ResponseEntity.ok(StripeApiResponse.builder()
                .stripePayment(payment)
                .build());
    }
}
