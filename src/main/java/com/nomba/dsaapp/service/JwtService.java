package com.nomba.dsaapp.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.UUID;

@Service
public class JwtService {

    @Value( "${jwt.secret}")
    private String secret;

    /**
     * Extracts the DSA (Direct Sales Agent) ID from the provided bearer token.
     * The DSA ID is expected to be stored as the subject in the JWT payload.
     *
     * @param bearerToken The bearer token containing the JWT with the DSA ID as the subject.
     * @return A UUID representing the DSA ID extracted from the token.
     */
    public UUID extractDsaId(String bearerToken) {
        String token = bearerToken.startsWith("Bearer ")
                ? bearerToken.substring(7)
                : bearerToken;

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        String subject = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        return UUID.fromString(subject);
    }
}
