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

    private Key decodedSecretKey;  // Decoded secret key



// ... other imports

    @PostConstruct
    public void init() {
        log.info("Entering @PostConstruct");

        // Load environment variable
        Dotenv dotenv = Dotenv.load();
        String localSecretKey = dotenv.get("JWT_SECRET_KEY");
        log.info("Secret Key: " + localSecretKey);

        // Decode the localSecretKey to byte array
        byte[] decodedKey = decodeSecretKey(localSecretKey);

        // Validate and initialize SECRET_KEY
        validateAndInitializeSecretKey(decodedKey, localSecretKey);

        log.info("Exiting @PostConstruct" + decodedSecretKey);
    }

    private byte[] decodeSecretKey(String key) {
        try {
            return Base64.getDecoder().decode(key);
        } catch (IllegalArgumentException e) {
            log.info("Invalid Base64 encoding. Generating a new key.");
            return new byte[0];
        }
    }

    private void validateAndInitializeSecretKey(byte[] decodedKey, String localSecretKey) {
        int keyLengthInBits = decodedKey.length * 8;
        String SECRET_KEY;
        if (localSecretKey == null || localSecretKey.isEmpty() || keyLengthInBits < 512) {
            log.info("SECRET_KEY is null, empty, or less than 512 bits. Generating a new one.");

            // Generate a secure key for HS512
            Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
            SECRET_KEY = Base64.getEncoder().encodeToString(key.getEncoded());

            log.info("Generated new SECRET_KEY of size: " + key.getEncoded().length * 8 + " bits");
        } else {
            SECRET_KEY = localSecretKey;
            log.info("Using existing SECRET_KEY of size: " + keyLengthInBits + " bits");
        }

        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        decodedSecretKey = Keys.hmacShaKeyFor(keyBytes);
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
