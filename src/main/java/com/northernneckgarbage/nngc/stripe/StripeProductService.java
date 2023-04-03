package com.northernneckgarbage.nngc.stripe;

import com.northernneckgarbage.nngc.dbConfig.StripeProductResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
                .imageUrl(product.getImages())
                .message("Product Retrieved")
                .build();
    }

    public StripeProductResponse<Product>  residential_trash_sub() throws StripeException {
        log.info("Residential_TR");
        Product product = Product.retrieve("prod_NTexfN4Dfxy2h4");
        Price price = Price.retrieve(product.getDefaultPrice());
        return StripeProductResponse.<Product>builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(price.getUnitAmount().toString())
                .imageUrl(product.getImages())
                .message("Product Retrieved")
                .build();
    }
    //When user clicks on this product, it will return the product id, name, description, and price
    public StripeProductResponse<Product> dumpster() throws StripeException {
        log.info("Dumpster");
        Product product = Product.retrieve("prod_NTzwClciqi6zCh");
        Price price = Price.retrieve("price_1Mj1wXACOG92rmQ4eB6LWple");
        return StripeProductResponse.<Product>builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(price.getUnitAmount().toString())
                .imageUrl(product.getImages())
                .message("Product Retrieved")
                .build();
    }

    public StripeProductResponse<Product>  residential_TOnce() throws StripeException {
        log.info("Residential_TL");
        Product product = Product.retrieve("prod_NTexfN4Dfxy2h4");
        Price price = Price.retrieve("price_1MiksRACOG92rmQ4nQev74WZ");

        return StripeProductResponse.<Product>builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(price.getUnitAmount().toString())
                .imageUrl(product.getImages())
                .message("Product Retrieved One Time Trash PickUp")
                .build();
    }
}
