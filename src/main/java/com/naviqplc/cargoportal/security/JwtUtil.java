package com.naviqplc.cargoportal.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private SecretKey secretKey;
    private long expirationMs;

    // Spring reads the two values from application.properties
    public JwtUtil(@Value("${app.jwt.secret}") String secret,
                   @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;
    }

    // 1. MAKE a token (called after a successful login)
    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)          // who
                .claim("role", role)        // extra info
                .issuedAt(now)              // created at
                .expiration(expiry)         // valid until
                .signWith(secretKey)        // the security stamp
                .compact();                 // turn into text
    }

    // 2. READ the username from a token
    public String getUsername(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    // 3. CHECK a token: real signature and not expired?
    public boolean isTokenValid(String token) {
        try {
            getUsername(token);     // throws an exception if fake or expired
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}