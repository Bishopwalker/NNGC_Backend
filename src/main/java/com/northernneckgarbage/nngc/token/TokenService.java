package com.northernneckgarbage.nngc.token;

import com.google.maps.errors.ApiException;
import com.northernneckgarbage.nngc.dbConfig.ApiResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.google.GeocodingService;
import com.northernneckgarbage.nngc.registration.auth.AuthenticationRequest;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.northernneckgarbage.nngc.repository.TokenRepository;
import com.northernneckgarbage.nngc.roles.AppUserRoles;
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
import java.util.Collections;


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
    public ApiResponse authenticate(AuthenticationRequest request) throws StripeException, IOException, InterruptedException, ApiException {
        var user = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        if(!user.isEnabled()){
    return ApiResponse.builder()
            .message("User is not enabled")
            .status("disabled")
            .build();
}



        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        if(user.getAppUserRoles() == AppUserRoles.USER){
            stripeService.createStripeCustomer(user.getId());




        }
        if(user.getZipCode() != null){
            //if it's null get the information
            geocodingService.getGeocodeByID(user.getId());
        }
        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return ApiResponse.builder()
                .token(Collections.singletonList(jwtToken))
                .message("User authenticated successfully")
                .customerDTO(user.toCustomerDTO())
                .status("enabled")
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
    ///tokens service to retrieve token by customer id that is not expired
    public ApiResponse<Customer> findByCustomerId(Long id) {
var token = tokenRepository.findAllValidTokenByUserNative(id);
//log.info(token.toString());

         return ApiResponse.<Customer>builder()
                 .token(Collections.singletonList(token.get(0).getToken()))
                 .build();
    }



    public void revokeAllUserTokens(Customer user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
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
        TokenConfirmationStatus confirmationStatus1 = null;

    // Find the token
    var userToken = tokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Token not found"));

   //check to see if token is expired
    if(LocalDateTime.now().isAfter(userToken.getExpiresAt())) {
        // Token is expired
        confirmationStatus1 = TokenConfirmationStatus.EXPIRED;
    }else{
        // Token is not expired
        confirmationStatus1 = TokenConfirmationStatus.SUCCESS;
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
                    .token(Collections.singletonList(token))
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
                    .token(Collections.singletonList(token))
                    .customerDTO(person.toCustomerDTO())
                    .build();

            // Set the confirmation status to SUCCESS
            confirmationStatus = TokenConfirmationStatus.SUCCESS;
        }

        return confirmationStatus;
    }

}
