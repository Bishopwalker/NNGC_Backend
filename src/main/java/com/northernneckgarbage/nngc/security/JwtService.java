package com.northernneckgarbage.nngc.security;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;

import static javax.crypto.Cipher.SECRET_KEY;

@Slf4j
@Service

public class JwtService {


    private String SECRET_KEY;
    private Key decodedSecretKey;  // Decoded secret key


    @PostConstruct
    public void init() throws NoSuchAlgorithmException {
        log.info("Entering @PostConstruct");

        // Load environment variable
        Dotenv dotenv = Dotenv.load();
//        log.info("Environment Variables: " + dotenv.entries());
        SECRET_KEY = dotenv.get("JWT_SECRET_KEY");
log.info("Secret Key: " + SECRET_KEY);
        // Validate and initialize SECRET_KEY
        if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
            log.info("SECRET_KEY is null or empty. Generating a new one.");
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA512");
            keyGen.init(512);  // for 512-bit key
            byte[] secretKey = keyGen.generateKey().getEncoded();
            SECRET_KEY = Base64.getEncoder().encodeToString(secretKey);
            log.info("Generated new SECRET_KEY of size: " + secretKey.length * 8 + " bits");
        } else {
            byte[] decodedKey = Base64.getDecoder().decode(SECRET_KEY);
            log.info("Using existing SECRET_KEY of size: " + decodedKey.length * 8 + " bits");
        }

        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        decodedSecretKey = Keys.hmacShaKeyFor(keyBytes);

        log.info("Exiting @PostConstruct" + decodedSecretKey);
    }
    public String extractUsername(String token) {

    return extractClaim(token, Claims::getSubject);
  }



    public String generateToken(UserDetails userDetails) {
        log.debug("Generating token for user: " + userDetails.getUsername()); // Debug log
        String token = Jwts
                .builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
        log.debug("Generated Token: " + token); // Debug log (be cautious about security)
        return token;
    }
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)  // Changed from HS256 to HS512
                .compact();
    }



    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
    }

   private Claims extractAllClaims(String token) {
     return Jwts
             .parserBuilder()
             .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
   }

    private Key getSigningKey() {
        log.debug(String.valueOf(decodedSecretKey)); // Debug log
        return decodedSecretKey;
    }

    //create a function to see if token isValid
   public Boolean isTokenValid(String token, UserDetails userDetails) {
       log.debug("Validating token for user: " + userDetails.getUsername()); // Debug log
       final String username = extractUsername(token);
       log.debug("Extracted username from token: " + username); // Debug log
       return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
   }
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

}
