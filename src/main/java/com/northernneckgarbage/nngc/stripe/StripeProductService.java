package com.northernneckgarbage.nngc.stripe;

import com.northernneckgarbage.nngc.dbConfig.StripeProductResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.ProductCollection;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
                "https://localhost:8080"
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
                .price(price.getUnitAmount())
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
                .price(price.getUnitAmount())
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
                .price(price.getUnitAmount())
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
                .price(price.getUnitAmount())
                .imageUrl(product.getImages())
                .message("Product Retrieved One Time Trash PickUp")
                .build();
    }
    public List<StripeProductResponse<Product>> getAllProducts() throws StripeException {
        log.info("Fetching all products");

        // Fetch all products from Stripe
        ProductCollection products = Product.list(new HashMap<>());

        // Process the products and build the response objects
        List<StripeProductResponse<Product>> productResponses = new ArrayList<>();
        for (Product product : products.getData()) {
            Price price = Price.retrieve(product.getDefaultPrice());
            StripeProductResponse<Product> response = StripeProductResponse.<Product>builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(price.getUnitAmount())
                    .imageUrl(product.getImages())
                    .message("Product Retrieved")
                    .build();
            productResponses.add(response);
        }

        return productResponses;
    }

}
