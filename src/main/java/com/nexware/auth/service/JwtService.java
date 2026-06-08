package com.nexware.auth.service;

import com.nexware.auth.security.AuthenticatedUser;
import com.nexware.config.ApplicationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final ApplicationProperties properties;
    private final Clock clock;
    private final SecretKey signingKey;

    public JwtService(ApplicationProperties properties) {
        this.properties = properties;
        this.clock = Clock.systemUTC();
        this.signingKey = Keys.hmacShaKeyFor(properties.security().jwt().secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(AuthenticatedUser user) {
        Instant now = Instant.now(clock);
        Instant expiresAt = now.plus(properties.security().jwt().accessTokenTtl());

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getUsername())
                .claims(Map.of(
                        "uid", user.getId().toString(),
                        "roles", user.getAuthorities().stream()
                                .map(Object::toString)
                                .toList()
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractTokenId(String token) {
        return parseClaims(token).getId();
    }

    public Instant extractExpiration(String token) {
        return parseClaims(token).getExpiration().toInstant();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && extractExpiration(token).isAfter(Instant.now(clock));
    }

    public long accessTokenExpiresInSeconds() {
        return properties.security().jwt().accessTokenTtl().toSeconds();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
