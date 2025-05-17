package com.example.back_PFE.jwt;

import com.example.back_PFE.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "MonSuperSecretKeyPourJWTQuiDoitEtreLongue1234567891011121314151617181920"; // Min 32 caractères
    private static final long ACCESS_EXPIRATION_TIME = 1000 * 60 * 60; // 1 heure
    private static final long REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 7 jours

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())// L'email comme identifiant
                .claim("id", user.getId())
                .claim("role", user.getRole().name()) // Ajout du rôle comme claim
                .setIssuedAt(new Date(System.currentTimeMillis())) // Date d'émission
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME)) // Expiration 10h
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Signature sécurisée
                .compact();
    }

    //  Génération du Refresh Token (7 jours)
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Utilisation de la clé sécurisée
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        final Claims claims = extractClaims(token);
        return claims.get("id", Long.class); // Extrait directement en tant que Long
    }
    public boolean validateToken(String token, User user) {
        return extractEmail(token).equals(user.getEmail());
    }
}
