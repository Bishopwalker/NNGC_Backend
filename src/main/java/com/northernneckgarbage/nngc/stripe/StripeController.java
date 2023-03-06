package com.northernneckgarbage.nngc.stripe;


import com.northernneckgarbage.nngc.dbConfig.StripeApiResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.checkout.Session;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("auth/stripe/")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
@RequiredArgsConstructor
public class StripeController {

    Dotenv dotenv = Dotenv.load();
     String stripePublicKey = dotenv.get("STRIPE_PUBLIC_KEY");

    private final StripeService stripeService;



    @PostMapping("/create-checkout-session")
    public String checkoutSession() throws StripeException {
        // Logic to create a Stripe session
        Session session = stripeService.createSession();
        return "redirect:" + session.getUrl();
    }
    @ExceptionHandler(StripeException.class)
    public ResponseEntity<StripeApiResponse> handleStripeException(StripeException ex) {
        return ResponseEntity.badRequest().body(StripeApiResponse.builder()
                .error(ex.getMessage())
                .build());
    }
}
