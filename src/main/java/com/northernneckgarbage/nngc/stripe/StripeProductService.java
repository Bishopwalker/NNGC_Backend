package com.northernneckgarbage.nngc.stripe;

import com.northernneckgarbage.nngc.dbConfig.StripeProductResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class StripeProductService {

    Dotenv dotenv = Dotenv.load();

    public StripeProductService(){
        log.info("StripeProductService");
        Stripe.apiKey = dotenv.get("STRIPE_SECRET_KEY");
        Stripe.setAppInfo(
                "NNGC-Server",
                "0.0.2",
                "http://localhost:5000"
        );
    }


    //function to retrive stripe product by id
    public StripeProductResponse<Product> retrieveProduct(String id) throws StripeException {
        log.info(id);
        Product product = Product.retrieve(id);
        Price price = Price.retrieve(product.getDefaultPrice());
        return StripeProductResponse.<Product>builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(price.getUnitAmount().toString())
                .message("Product Retrieved")
                .build();
    }

}
