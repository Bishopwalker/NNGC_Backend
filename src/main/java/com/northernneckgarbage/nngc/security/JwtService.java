package com.northernneckgarbage.nngc.security;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Value;
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

    private final Key decodedSecretKey;  // Decoded secret key

        public JwtService() {
            Dotenv dotenv = Dotenv.load();
            // Secret key from application properties
            String secretKey = dotenv.get("JWT_SECRET_KEY");
            // Decode the secret key from Base64 to a byte array
            byte[] decodedKey = decodeSecretKey(secretKey);
            log.info(Arrays.toString(decodedKey));
            // Initialize decodedSecretKey with the final secret key
            decodedSecretKey = Keys.hmacShaKeyFor(decodedKey);
            log.info(String.valueOf(decodedSecretKey));
        }

        private byte[] decodeSecretKey(String key) {
            try {
                return Base64.getDecoder().decode(key);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid Base64 encoding for JWT_SECRET_KEY");
            }
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
