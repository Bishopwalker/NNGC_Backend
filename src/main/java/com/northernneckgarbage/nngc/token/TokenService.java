package com.northernneckgarbage.nngc.token;

import com.northernneckgarbage.nngc.dbConfig.ApiResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.registration.auth.AuthenticationRequest;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.northernneckgarbage.nngc.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private final CustomerRepository customerRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ApiResponse authenticate(AuthenticationRequest request){
        var user = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return ApiResponse.builder()
                .token(jwtToken)
                .message("User authenticated successfully")
                .customerDTO(user.toCustomerDTO())
//                .customer(user)
                .build();
    }


    public void saveUserToken(Customer user, String jwtToken) {
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

    public void revokeAllUserTokens(Customer user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser((int) user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
            token.setExpiresAt(LocalDateTime.now());
        });
        tokenRepository.saveAll(validUserTokens);
    }






    public ApiResponse confirmToken(String token) {
        var userToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));
  //findByID
        var person = customerRepository.findById(userToken.getCustomer().getId())
                .orElseThrow(() -> new RuntimeException("Person not found"));
        if(!person.isEnabled()) {
            person.setEnabled(true);
            customerRepository.save(person);
        }
        log.warn("Token found: "+userToken);
        log.warn("Person found: "+person);

        if (userToken.getConfirmedAt() != null)
            throw new RuntimeException("Token already confirmed");
        if(LocalDateTime.now().isAfter(userToken.getExpiresAt()))
            throw new RuntimeException("Token expired");

        userToken.setConfirmedAt(LocalDateTime.now());
        userToken.setExpiresAt(LocalDateTime.now().plusMinutes(45));
        tokenRepository.save(userToken);
        return ApiResponse.builder()
                .token(token)
                .customerDTO(person.toCustomerDTO())
               // .customer(person)
                .build();
    }
}
