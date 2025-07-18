package me.oreos.iam.services.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import me.oreos.iam.services.TokenProvider;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service 
public class JwtService implements TokenProvider {

    // Use at least 256-bit (32-byte) key for HS256
    private final String SECRET = "your-very-secure-and-long-secret-key-for-jwt-token-123!";
    private final long EXPIRATION_MILLIS = 3600_000; // 1 hour

    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * Generate JWT token with subject (e.g., user ID or email)
     */
    @Override
	public String generateToken(Object subject) {
        return generateToken(subject, null);
    }

    /**
     * Generate JWT token with subject and custom claims
     */
    @Override
	public String generateToken(Object subject, Map<String, Object> claims) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiry = new Date(nowMillis + EXPIRATION_MILLIS);

        JwtBuilder builder = Jwts.builder()
                .setSubject(subject.toString())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SECRET_KEY);

        if (claims != null && !claims.isEmpty()) {
            builder.addClaims(claims);
        }

        return builder.compact();
    }

    /**
     * Validate JWT token and return claims if valid, null otherwise
     */
    @Override
	public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            // invalid token: expired, malformed, unsupported, or signature doesn't match
            return null;
        }
    }

    /**
     * Check if token is expired
     */
    @Override
	public boolean isTokenExpired(String token) {
        Claims claims = validateToken(token);
        return claims == null || claims.getExpiration().before(new Date());
    }

    /**
     * Check if token is expired
     */
    @Override
	public boolean isTokenExpired(Claims claims) {
        return claims == null || claims.getExpiration().before(new Date());
    }

    /**
     * Get subject (e.g., user ID or email) from token
     */
    @Override
	public String getSubject(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.getSubject() : null;
    }
}
