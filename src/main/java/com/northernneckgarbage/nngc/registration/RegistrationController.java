package com.northernneckgarbage.nngc.registration;

import com.northernneckgarbage.nngc.dbConfig.ApiResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.registration.auth.AuthenticationRequest;
import com.northernneckgarbage.nngc.service.CustomerService;
import com.northernneckgarbage.nngc.token.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("auth/nngc/")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class RegistrationController {

    private final CustomerService customerService;

    private final RegistrationService service;
    private final TokenService tokenService;


    @PostMapping("/register")
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
    ) {
        return ResponseEntity.ok(service.register(request));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(tokenService.authenticate(request));
    }

    @GetMapping("/confirm")
    public String confirmMail(@RequestParam("token") String token) {
       tokenService.confirmToken(token);
        return "confirmed";
    }

}
