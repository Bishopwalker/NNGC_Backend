package com.northernneckgarbage.nngc.registration.auth;

import com.northernneckgarbage.nngc.dbConfig.GoogleApiResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.northernneckgarbage.nngc.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class GoogleLoginController {
    private final JwtService jwtService;
    private final CustomerRepository repo;

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }
    @GetMapping("/google")
    public ResponseEntity<GoogleApiResponse> getUser(Principal principal){
        //return principal;
        log.info("Principal"+principal);
        return ResponseEntity.ok(new GoogleApiResponse(principal));
    }

    @GetMapping("/logout")
    public void logout(){
        SecurityContextHolder.clearContext();
    }

    //redirect to login
    @GetMapping("/")
    public ResponseEntity<String> login(@RequestParam("token") String token ){


        final String userEmail = jwtService.extractUsername(token);

     Optional<Customer> customer = repo.findByEmail(userEmail);

        if(customer.get().isEnabled()==true){
            log.info("Customer"+customer + "is enabled");
            return  ResponseEntity.ok("redirect:http://localhost:5173/");
        }
        else{
            log.info("Customer"+customer + "is not enabled");
            return ResponseEntity.ok("redirect:http://localhost:8080/login");
        }

    }

}
