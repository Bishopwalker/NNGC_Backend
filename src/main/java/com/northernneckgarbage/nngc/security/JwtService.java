package com.northernneckgarbage.nngc.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    // Map to store token -> secretKey pairs
    private final ConcurrentHashMap<String, SecretKey> tokenKeys = new ConcurrentHashMap<>();

    // Generate a new secret key
    private SecretKey generateNewKey() {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        log.info("Generated new JWT Secret Key (Base64): {}",
                Base64.getEncoder().encodeToString(key.getEncoded()));
        return key;
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails) {
        SecretKey newKey = generateNewKey();
        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 3)) // 3 hours
                .signWith(newKey, SignatureAlgorithm.HS512)
                .compact();

        // Store the key associated with this token
        tokenKeys.put(token, newKey);
        return token;
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        SecretKey newKey = generateNewKey();
        String token = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(newKey, SignatureAlgorithm.HS512)
                .compact();

        // Store the key associated with this token
        tokenKeys.put(token, newKey);
        return token;
    }

    private Claims extractAllClaims(String token) {
        SecretKey key = tokenKeys.get(token);
        if (key == null) {
            throw new JwtException("No key found for token");
        }
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            if (!isValid) {
                // Clean up the stored key if token is invalid
                tokenKeys.remove(token);
            }
            return isValid;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            // Clean up the stored key if token is invalid
            tokenKeys.remove(token);
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        boolean isExpired = expiration.before(new Date());
        if (isExpired) {
            // Clean up the stored key if token is expired
            tokenKeys.remove(token);
        }
        return isExpired;
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    }