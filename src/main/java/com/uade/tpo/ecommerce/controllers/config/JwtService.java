package com.uade.tpo.ecommerce.controllers.config;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;

import io.jsonwebtoken.JwtException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

@Service
public class JwtService {

    @Value("${application.security.jwt.secretKey}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    // 🔹 Genera token con username y rol
    public String generateToken(String username, String role) {
        return buildToken(username, role, jwtExpiration);
    }

    // 🔹 Mantengo compatibilidad con la versión anterior
    public String generateTokenFromUsername(String username) {
        return buildToken(username, null, jwtExpiration);
    }

    private String buildToken(String username, String role, long expiration) {
        var builder = Jwts.builder()
                .subject(username) // usa el email como subject
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey());

        // ✅ Si se pasó un rol, lo agregamos al payload
        if (role != null && !role.isEmpty()) {
            builder.claim("role", role);
        }

        return builder.compact();
    }

    // 🔹 Valida firma y expiración
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token); // ya exige firma válida
            return username != null && !isTokenExpired(token);
        } catch (JwtException ex) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 🔹 Nuevo método: obtener el rol directamente
    public String extractRole(String token) {
        try {
            return extractAllClaims(token).get("role", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @PostConstruct
    void logKeyLen() {
        System.out.println("[JWT] secret length (chars): " + secretKey.length());
    }
}
