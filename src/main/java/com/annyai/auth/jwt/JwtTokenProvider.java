package com.annyai.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    public static final String CLAIM_ROLE = "role";

    private final JwtProperties jwtProperties;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.algorithm = Algorithm.HMAC512(jwtProperties.secretKey());
        this.verifier = JWT.require(algorithm)
                //.acceptLeeway(1)
                .build();
    }

    public String generateToken(String email, String role) {
        return JWT.create()
                .withSubject(email)
                .withClaim(CLAIM_ROLE, role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.expiration()))
                .sign(algorithm);
    }

    public String getEmailFromToken(String token) {
        return decodeToken(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return decodeToken(token).getClaim(CLAIM_ROLE).asString();
    }

    public boolean validateToken(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    private DecodedJWT decodeToken(String token) {
        return verifier.verify(token);
    }
}