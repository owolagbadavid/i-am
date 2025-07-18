package me.oreos.iam.services;

import java.util.Map;

import io.jsonwebtoken.Claims;

public interface TokenProvider {

    /**
     * Generate JWT token with subject (e.g., user ID or email)
     */
    String generateToken(Object subject);

    /**
     * Generate JWT token with subject and custom claims
     */
    String generateToken(Object subject, Map<String, Object> claims);

    /**
     * Validate JWT token and return claims if valid, null otherwise
     */
    Claims validateToken(String token);

    /**
     * Check if token is expired
     */
    boolean isTokenExpired(String token);

    /**
     * Check if token is expired
     */
    boolean isTokenExpired(Claims claims);

    /**
     * Get subject (e.g., user ID or email) from token
     */
    String getSubject(String token);

}