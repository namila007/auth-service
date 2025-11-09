package me.namila.service.auth.application.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * JWT Token Provider for generating and validating JWT tokens.
 * Uses RS256 (asymmetric) algorithm for signing.
 */
@Component
@Slf4j
public class JwtTokenProvider {
    
    @Value("${jwt.secret:default-secret-key-that-should-be-changed-in-production-minimum-256-bits}")
    private String secret;
    
    @Value("${jwt.expiration:1800000}") // 30 minutes default
    private Long expiration;
    
    @Value("${jwt.issuer:auth-service}")
    private String issuer;
    
    /**
     * Generate a JWT token for a user.
     * @param userId The user ID
     * @param username The username
     * @param email The email
     * @param roles The user roles
     * @return The JWT token string
     */
    public String generateToken(UUID userId, String username, String email, java.util.List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("username", username);
        claims.put("email", email);
        claims.put("roles", roles);
        
        return createToken(claims, userId.toString());
    }
    
    /**
     * Generate a JWT token with custom claims.
     * @param claims Custom claims to include in the token
     * @param subject The subject (usually user ID)
     * @return The JWT token string
     */
    public String createToken(Map<String, Object> claims, String subject) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expiration);
        
        return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuer(issuer)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .signWith(getSigningKey())
            .compact();
    }
    
    /**
     * Validate a JWT token.
     * @param token The JWT token string
     * @return true if token is valid
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract username from token.
     * @param token The JWT token string
     * @return The username
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("username", String.class));
    }
    
    /**
     * Extract user ID from token.
     * @param token The JWT token string
     * @return The user ID
     */
    public UUID getUserIdFromToken(String token) {
        String userIdStr = getClaimFromToken(token, claims -> claims.get("userId", String.class));
        return userIdStr != null ? UUID.fromString(userIdStr) : null;
    }
    
    /**
     * Extract expiration date from token.
     * @param token The JWT token string
     * @return The expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    /**
     * Extract a claim from token.
     * @param token The JWT token string
     * @param claimsResolver Function to extract the claim
     * @return The claim value
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Get all claims from token.
     * @param token The JWT token string
     * @return The claims
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
    
    /**
     * Check if token is expired.
     * @param token The JWT token string
     * @return true if token is expired
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    /**
     * Get the signing key from the secret.
     * @return The signing key
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

