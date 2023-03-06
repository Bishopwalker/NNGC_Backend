package com.northernneckgarbage.nngc.stripe;


import com.northernneckgarbage.nngc.dbConfig.StripeApiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.stripe.Stripe;
import io.github.cdimascio.dotenv.Dotenv;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class StripeService {
Dotenv dotenv = Dotenv.load();
public StripeService() {
    Stripe.apiKey = dotenv.get("STRIPE_SECRET_KEY");
    Stripe.setAppInfo(
            "NNGC",
            "0.0.2",
            "http://localhost:8080"
    );

}


    public StripeApiResponse charge(StripePayment payment) throws StripeException {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", payment.getAmount());
        chargeParams.put("currency", payment.getCurrency());
        chargeParams.put("description", payment.getDescription());
        chargeParams.put("email", payment.getStripeEmail());
        chargeParams.put("source", dotenv.get("STRIPE_SECRET_KEY"));
        return StripeApiResponse.builder()
                .stripePayment(payment)
                .data(chargeParams)
                .charge(Charge.create(chargeParams))
                .build() ;
    }


    public Session createSession() throws StripeException {
        // This is your test secret API key.
      //  Stripe.apiKey = "sk_test_51MiJlWACOG92rmQ4BY6VTYcXZQUBTsHzKkrG96OujKC6W1HBSOUMXCYIN9tgHZDpWjkyUcGzAYOZtAKGoS1oOmmE00cOVU7uIO";

        String YOUR_DOMAIN = "http://www.northernneckgarbage.com";
        SessionCreateParams params =
                SessionCreateParams.builder()
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
                                        .setPrice("price_1MihduACOG92rmQ4EdqjAwV1")
                                        .build())
                        .build();
        return Session.create(params);
    }



}
