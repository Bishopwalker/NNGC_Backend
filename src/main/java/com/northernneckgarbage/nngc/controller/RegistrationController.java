package com.northernneckgarbage.nngc.controller;


import com.google.maps.errors.ApiException;
import com.northernneckgarbage.nngc.dbConfig.ApiResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.registration.RegistrationRequest;
import com.northernneckgarbage.nngc.registration.RegistrationService;
import com.northernneckgarbage.nngc.registration.auth.AuthenticationRequest;
import com.northernneckgarbage.nngc.repository.TokenRepository;
import com.northernneckgarbage.nngc.service.CustomerService;
import com.northernneckgarbage.nngc.token.Token;
import com.northernneckgarbage.nngc.token.TokenService;
import com.stripe.exception.StripeException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("auth/nngc/")
@RequiredArgsConstructor
public class RegistrationController {
    private final TokenRepository tokenRepository;

    private final CustomerService customerService;

    private final RegistrationService service;
    private final TokenService tokenService;

    @Value("${spring.profiles.active}")
    private String env;
    @PostMapping("admin/register")
    public String processRegister(@RequestBody Customer customer) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
        log.info("New customer object created"+customer);
        customerService.addCustomer(customer);
        return "register_success";
    }

    @GetMapping("admin/tokens/{id}")
    public ResponseEntity<List<Token>> getAllTokensForUserById(@RequestHeader("Authorization") String headers, @PathVariable long id) {
        var user=tokenRepository.findByToken(headers).get().getCustomer().getAppUserRoles();
        if(user==null){
            return ResponseEntity.badRequest().body(null);
        }
        if(user.toString().equals("ADMIN")){
            return ResponseEntity.ok(tokenRepository.findAllValidTokenByUser(id));
        }
        return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("registration")
    public ResponseEntity<ApiResponse> register(
            @RequestBody RegistrationRequest request
    ) throws IOException {
        log.info(request.getService());
        return ResponseEntity.ok(service.register(request));
    }

    @GetMapping("/resend-token/{email}")
    public ResponseEntity<ApiResponse> resendToken(@PathVariable String email) throws IOException {
        return ResponseEntity.ok(service.resendToken(email));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) throws StripeException, IOException, InterruptedException, ApiException {
        return ResponseEntity.ok(tokenService.authenticate(request));
    }


    @GetMapping("/confirm")
    public void confirmMail(@RequestParam("token") String token, HttpServletResponse response) throws StripeException, IOException, InterruptedException, ApiException {
        // Call the confirmToken method from the TokenService and get the status
        TokenService.TokenConfirmationStatus confirmationStatus = tokenService.confirmToken(token);
log.info(String.valueOf(isProduction()));
        // Handle different confirmation statuses
        String redirectUrl = switch (confirmationStatus) {
            case SUCCESS ->
                // Redirect to your website or return a success message
                    isProduction() ? "https://www.northernneckgarbage.com/success" : "http://localhost:5173/success";
            case ALREADY_CONFIRMED ->
                // Redirect to your website or return a message indicating the token is already confirmed
                    isProduction() ? "https://www.northernneckgarbage.com/already-confirmed" : "http://localhost:5173/already-confirmed";
            case EXPIRED ->
                // Redirect to an expired token page
                    isProduction() ? "https://www.northernneckgarbage.com/expired" : "http://localhost:5173/expired";
            // Redirect to a generic error page
        };
        response.sendRedirect(redirectUrl);
    }

    //endpoint to retrieve a token by customer ID
    @GetMapping("/token/{id}")
    public ResponseEntity<ApiResponse<Customer>> getToken(@PathVariable("id") Long id) {
        log.info("id: " + id);
        // Call the getToken method from the TokenService and get the token

      // value =tokenService.findByCustomerId(id);
       var token = tokenService.findByCustomerId(id);
        // Return the response entity with the token
        return ResponseEntity.ok(ApiResponse.<Customer>builder()
              .token(token.getToken())
                .build());
    }

    @GetMapping("/token_status")
    public ResponseEntity<String> tokenStatus(@RequestParam("token") String token) {
        // Call the confirmToken method from the TokenService and get the status
        TokenService.TokenConfirmationStatus confirmationStatus = tokenService.tokenStatus(token);
        log.info(String.valueOf(isProduction()));

        if (confirmationStatus == null) {
            log.error("Token confirmation status is null for token: " + token);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error determining token status");
        }

        // Determine the response body based on the confirmation status
        String responseBody = switch (confirmationStatus) {
            case EXPIRED -> "expired";
            case SUCCESS, ALREADY_CONFIRMED -> "good";
        };

        // Debug logging to trace the response body
        log.info("Response Body: " + responseBody);

        // Return the response entity with the determined body
        return ResponseEntity.ok(responseBody);
    }




    private boolean isProduction() {
        // Implement your logic to determine if the application is running in production
        // For example, you can check an environment variable
        return "prod".equals(env);
    }

    @GetMapping("/google/login")
    public String redirectToGoogle() {
        // Redirect to the URL that initiates OAuth2 login with Google
        return "redirect:/oauth2/authorization/google";
    }


    @GetMapping("/loginSuccess")
    public String getLoginInfo(@NotNull OAuth2AuthenticationToken authentication) {
        OAuth2User oAuth2User = authentication.getPrincipal();
        String name = oAuth2User.getAttribute("name");
        log.info("name: " + name);
        String email = oAuth2User.getAttribute("email");
        log.info("email: " + email);
        // add more attributes as needed
        return "Hello, " + name + "!" + " Your email is " + email;
    }

    @GetMapping("/google/login/error")
    public ResponseEntity<?> googleLoginError(@RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            log.error("Google login error occurred: " + error);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Google login failed. Please try again or contact support if the problem persists.");
        }

        // If there's no error, handle accordingly (redirect or another action)
        return ResponseEntity.ok("No error detected. Redirecting...");
    }


}
