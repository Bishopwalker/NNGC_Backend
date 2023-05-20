package com.northernneckgarbage.nngc.registration;


import com.northernneckgarbage.nngc.dbConfig.ApiResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.registration.auth.AuthenticationRequest;
import com.northernneckgarbage.nngc.service.CustomerService;
import com.northernneckgarbage.nngc.token.TokenService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpHeaders;


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
  public ResponseEntity<ApiResponse> confirmMail(@RequestParam("token") String token) throws StripeException {
    return ResponseEntity.ok(tokenService.confirmToken(token));
    }

    @GetMapping("/login/google")
    public ResponseEntity<?> redirectToGoogle() {
        String redirectUrl = "http://localhost:5000/oauth2/authorization/google"; // replace with your redirect URL
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
