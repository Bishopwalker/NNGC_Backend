package com.northernneckgarbage.nngc.token;

import com.google.maps.errors.ApiException;
import com.northernneckgarbage.nngc.dbConfig.ApiResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.google.GeocodingService;
import com.northernneckgarbage.nngc.registration.auth.AuthenticationRequest;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.northernneckgarbage.nngc.security.JwtService;
import com.northernneckgarbage.nngc.stripe.StripeService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;



@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private final CustomerRepository customerRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final StripeService stripeService;
    private final GeocodingService geocodingService;

    public enum TokenConfirmationStatus {
        SUCCESS,
        ALREADY_CONFIRMED,
        EXPIRED

    }
    public ApiResponse authenticate(AuthenticationRequest request){
        var user = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return ApiResponse.builder()
                .token(jwtToken)
                .message("User authenticated successfully")
                .customerDTO(user.toCustomerDTO())
//                .customer(user)
                .build();
    }


    public void saveUserToken(Customer user, String jwtToken) {
        if(!user.isEnabled()){
            var token = Token.builder()
                    .customer(user)
                    .token(jwtToken)
                    .tokenType(TokenType.BEARER)
                    .expired(false)
                    .createdAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusDays(3))
                    .revoked(false)
                    .build();
            tokenRepository.save(token);
            return;
        }
        var token = Token.builder()
                .customer(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(45))
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(Customer user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser((int) user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
            token.setExpiresAt(LocalDateTime.now());
        });
        tokenRepository.saveAll(validUserTokens);

    }
//building a function just to check the token status
public TokenConfirmationStatus tokenStatus(String token)  {
        TokenConfirmationStatus confirmationStatus1;

    // Find the token
    var userToken = tokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Token not found"));

   //check to see if token is expired
    if(LocalDateTime.now().isAfter(userToken.getExpiresAt())) {
        // Token is expired
        confirmationStatus1 = TokenConfirmationStatus.EXPIRED;
    }

return confirmationStatus1;
}




    public TokenConfirmationStatus confirmToken(String token) throws StripeException, IOException, InterruptedException, ApiException {
        // Initialize the variable to hold the token confirmation status
        TokenConfirmationStatus confirmationStatus;

        // Find the token
        var userToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        // Find the associated person (customer)
        var person = customerRepository.findById(userToken.getCustomer().getId())
                .orElseThrow(() -> new RuntimeException("Person not found"));

        // Check if the person is already enabled
        if (!person.isEnabled()) {
            // Enable the person and save
            person.setEnabled(true);
            customerRepository.save(person);

            // Create a Stripe customer
            stripeService.createStripeCustomer(person.getId());

            // Fetch and save Geocode information
            geocodingService.getGeocodeByID(person.getId());

            // Build the ApiResponse (you might want to do something with this)
            ApiResponse.builder()
                    .token(token)
                    .customerDTO(person.toCustomerDTO())
                    .message("User confirmed and enabled. Stripe customer created")
                    .status("success adding geocode")
                    .build();

            // Set the confirmation status to SUCCESS
            confirmationStatus = TokenConfirmationStatus.SUCCESS;
        } else if (userToken.getConfirmedAt() != null) {
            // Token is already confirmed
            confirmationStatus = TokenConfirmationStatus.ALREADY_CONFIRMED;
        } else if (LocalDateTime.now().isAfter(userToken.getExpiresAt())) {
            // Token is expired
            confirmationStatus = TokenConfirmationStatus.EXPIRED;
        } else {
            // Confirm the token and save
            userToken.setConfirmedAt(LocalDateTime.now());
            userToken.setExpiresAt(LocalDateTime.now().plusMinutes(45));
            tokenRepository.save(userToken);

            // Build the ApiResponse (you might want to do something with this)
            ApiResponse.builder()
                    .token(token)
                    .customerDTO(person.toCustomerDTO())
                    .build();

            // Set the confirmation status to SUCCESS
            confirmationStatus = TokenConfirmationStatus.SUCCESS;
        }

        return confirmationStatus;
    }

}
