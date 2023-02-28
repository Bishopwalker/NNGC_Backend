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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;

import static java.time.LocalTime.now;

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
                .orElseThrow();


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
                .customer(user)
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





//    public void confirmToken(String token) {
//        var userToken = tokenRepository.findByToken(token)
//                .orElseThrow(() -> new IllegalArgumentException("Token not found"));
//
//        log.info("Token found: "+userToken);
//
//        if (userToken.getConfirmedAt() != null)
//            throw new IllegalArgumentException("Token already confirmed");
//        if(LocalDateTime.now().isAfter(userToken.getExpiresAt()))
//            throw new IllegalArgumentException("Token expired");
//
//        userToken.setConfirmedAt(LocalDateTime.now());
//        userToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
//        tokenRepository.save(userToken);
//    }
    //confirmToken function that returns an APiResponse

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
        userToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        tokenRepository.save(userToken);
        return ApiResponse.builder()
                .token(token)
                .customer(person)
                .build();
    }
}
