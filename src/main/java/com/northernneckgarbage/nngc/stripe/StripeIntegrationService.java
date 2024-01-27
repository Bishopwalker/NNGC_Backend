package com.northernneckgarbage.nngc.stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.terminal.Reader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StripeIntegrationService {

    private final StripeTerminalService stripeTerminalService;
    private final StripeService stripeService;

    public StripeIntegrationService(StripeTerminalService stripeTerminalService, StripeService stripeService) {
        this.stripeTerminalService = stripeTerminalService;
        this.stripeService = stripeService;
    }

    public void processPaymentWithSimulatedTerminal(long amount, String currency, String description, String locationId, String registrationCode) throws StripeException {
        // Create a simulated reader
        Reader reader = stripeTerminalService.createSimulatedReader(locationId, registrationCode);

        // Create a payment intent for the terminal
        PaymentIntent paymentIntent = stripeService.createPaymentIntentForTerminal(amount, currency, description);

        // Since it's a simulated reader, here you would simulate the process of card payment
        // In a real scenario, you would interact with the reader to complete the transaction
        log.info("Simulated transaction completed with PaymentIntent: {}", paymentIntent.getId());
    }
}
