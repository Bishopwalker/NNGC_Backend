package com.northernneckgarbage.email;

import org.springframework.stereotype.Service;

@Service
public class EmailValidator {
    public static boolean test(String email) {
        var regex = "^(.+)@(.+)$";
        return email.matches(regex);
    }
}
