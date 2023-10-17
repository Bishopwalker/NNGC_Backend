package com.northernneckgarbage.nngc.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException, ServletException, IOException {
        if (exception instanceof BadCredentialsException) {
            // Handle wrong email or password
            // Set response message or code
        } else if (exception instanceof DisabledException) {
            // Handle not enabled
            // Set response message or code
        } else {
            // Handle other authentication exceptions
            // Set response message or code
        }

        super.onAuthenticationFailure(request, response, exception);
    }
}
