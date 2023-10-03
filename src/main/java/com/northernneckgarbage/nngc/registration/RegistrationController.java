package com.northernneckgarbage.nngc.registration;


import com.google.maps.errors.ApiException;
import com.northernneckgarbage.nngc.dbConfig.ApiResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.registration.auth.AuthenticationRequest;
import com.northernneckgarbage.nngc.service.CustomerService;
import com.northernneckgarbage.nngc.token.TokenService;
import com.stripe.exception.StripeException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Controller
@RequestMapping("auth/nngc/")
@RequiredArgsConstructor
public class RegistrationController {

    private final CustomerService customerService;

    private final RegistrationService service;
    private final TokenService tokenService;


    @PostMapping("admin/register")
    public String processRegister(@RequestBody Customer customer) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
        log.info("New customer object created"+customer);
        customerService.addCustomer(customer);
        return "register_success";
    }

    @PostMapping("registration")
    public ResponseEntity<ApiResponse> register(
            @RequestBody RegistrationRequest request
    ) throws IOException {
        return ResponseEntity.ok(service.register(request));
    }

    @GetMapping("/resend-token/{email}")
    public ResponseEntity<ApiResponse> resendToken(@PathVariable String email) throws IOException {
        return ResponseEntity.ok(service.resendToken(email));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(tokenService.authenticate(request));
    }


    @GetMapping("/confirm")
    public void confirmMail(@RequestParam("token") String token, HttpServletResponse response) throws StripeException, IOException, InterruptedException, ApiException {
        // Call the confirmToken method from the TokenService and get the status
        TokenService.TokenConfirmationStatus confirmationStatus = tokenService.confirmToken(token);

        // Handle different confirmation statuses
        String redirectUrl;
        switch (confirmationStatus) {
            case SUCCESS:
                // Redirect to your website or return a success message
                redirectUrl = isProduction() ? "http://www.northernneckgarbage.com" : "http://localhost:5173";
                break;
            case ALREADY_CONFIRMED:
                // Redirect to your website or return a message indicating the token is already confirmed
                redirectUrl = isProduction() ? "http://www.northernneckgarbage.com/already-confirmed" : "http://localhost:5173/already-confirmed";
                break;
            case EXPIRED:
                // Redirect to an expired token page
                redirectUrl = isProduction() ? "http://www.northernneckgarbage.com/expired" : "http://localhost:5173/expired";
                break;
            default:
                // Redirect to a generic error page
                redirectUrl = isProduction() ? "http://www.northernneckgarbage.com " : "http://localhost:5173";
                break;
        }
        response.sendRedirect(redirectUrl);
    }

    private boolean isProduction() {
        // Implement your logic to determine if the application is running in production
        // For example, you can check an environment variable
        return false;
    }
    @GetMapping("/google/login")
    public ResponseEntity<?> redirectToGoogle() {
        String redirectUrl = isProduction() ? "http://www.northernneckgarbage.com/login/oauth2/code/google" : "http://3.85.8.238:5000/login/oauth2/code/google";
        URI uri = UriComponentsBuilder.fromUriString(redirectUrl).build().toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uri);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

    @GetMapping("/loginSuccess")
    public String getLoginInfo(OAuth2AuthenticationToken authentication) {
        OAuth2User oAuth2User = authentication.getPrincipal();
        String name = oAuth2User.getAttribute("name");
        log.info("name: " + name);
        String email = oAuth2User.getAttribute("email");
        log.info("email: " + email);
        // add more attributes as needed
        return "Hello, " + name + "!" + " Your email is " + email;
    }




}
