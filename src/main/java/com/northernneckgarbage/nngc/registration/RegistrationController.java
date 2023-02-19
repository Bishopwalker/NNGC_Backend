package com.northernneckgarbage.nngc.registration;

import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.northernneckgarbage.nngc.security.AuthUserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("auth/nngc/")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class RegistrationController {

    private final RegistrationService registrationService;


    @PostMapping(path = "/register")
    public String register(@RequestBody RegistrationRequest request) {
                registrationService.register(request);
        return "register_success";

    }


}
