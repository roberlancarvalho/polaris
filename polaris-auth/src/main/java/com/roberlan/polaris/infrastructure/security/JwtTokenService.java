package com.roberlan.polaris.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.roberlan.polaris.core.domain.model.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtTokenService {
    
    private final String SECRET_KEY = "TechNorth_Secret_Key_Super_Segura_2025";
    private final String ISSUER = "Polaris-API";
    
    public String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.create().withIssuer(ISSUER).withSubject(user.getEmail()).withClaim(
                "role", user.getRole().name()).withClaim("name", user.getName()).withExpiresAt(
                Instant.now().plus(2, ChronoUnit.HOURS)).sign(algorithm);
    }
    
    public String validateTokenAndGetSubject(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            return JWT.require(algorithm).withIssuer(ISSUER).build().verify(token).getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }
}