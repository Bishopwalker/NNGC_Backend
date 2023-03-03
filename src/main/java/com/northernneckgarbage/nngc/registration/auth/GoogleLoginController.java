package com.northernneckgarbage.nngc.registration.auth;

import com.northernneckgarbage.nngc.dbConfig.GoogleApiResponse;
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

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class GoogleLoginController {

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
    public String login(){
        if(isAuthenticated()){
            return "redirect:http://localhost:5173/";
        }
        log.info("Redirecting to login");
        return "redirect:http://localhost:5173/";
    }

}
