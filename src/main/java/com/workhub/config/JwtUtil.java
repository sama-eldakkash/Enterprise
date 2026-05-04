package com.workhub.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    public static final String CLAIM_TENANT_ID = "tenantId";
    public static final String CLAIM_ROLE = "role";

    private final String secret = "mysecretkeymysecretkeymysecretkey12";
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes());

    public String generateToken(String email, Long tenantId, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim(CLAIM_TENANT_ID, tenantId)
                .claim(CLAIM_ROLE, role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60))
                .signWith(key)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public Long extractTenantId(String token) {
        Claims claims = parseClaims(token);
        Object raw = claims.get(CLAIM_TENANT_ID);
        if (raw instanceof Number n) {
            return n.longValue();
        }
        return null;
    }

    public String extractRole(String token) {
        Object r = parseClaims(token).get(CLAIM_ROLE);
        return r != null ? r.toString() : null;
    }

    public Claims extractClaims(String token) {
        return parseClaims(token);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
