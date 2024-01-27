package com.northernneckgarbage.nngc.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.terminal.Reader;
import com.stripe.param.terminal.ReaderCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StripeTerminalService {

    public StripeTerminalService() {
        log.info("StripeTerminalService initialized");
        Dotenv dotenv = Dotenv.load();
        Stripe.apiKey = dotenv.get("STRIPE_SECRET_KEY");
        Stripe.setAppInfo(
                "NNGC-Server",
                "0.0.2",
                "https://api.northernneckgarbage.com"
        );
    }

    public Reader createSimulatedReader(String locationId, String registrationCode) throws StripeException {
        log.info("Creating simulated reader: Location ID - {}, Registration Code - {}", locationId, registrationCode);
        ReaderCreateParams params = ReaderCreateParams.builder()
                .setLocation(locationId)
                .setRegistrationCode(registrationCode)
                .build();

        return Reader.create(params);
    }

    public Reader retrieveReader(String readerId) throws StripeException {
        log.info("Retrieving reader: ID - {}", readerId);
        return Reader.retrieve(readerId);
    }
}
