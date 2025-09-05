package com.fusionsoft.cnd.com.auth.security.util;

import com.fusionsoft.cnd.com.auth.domain.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {

    private final SecretKey key;
    @Getter
    private final long accessTokenValiditySeconds;
    @Getter
    private final long refreshTokenValiditySeconds;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-seconds}") long accessTokenValiditySeconds,
            @Value("${jwt.refresh-token-validity-seconds}") long refreshTokenValiditySeconds
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public String generateAccessToken(String username, Set<Role> roles) {
        Instant now = Instant.now();

        Set<String> roleNames = roles.stream()
                .map(role -> role.getRoleName().name()) // ROLE_ADMIN, ROLE_USER …
                .collect(Collectors.toSet());

        return Jwts.builder()
                .subject(username)
                .issuer("http://auth-service.cnd-dev.svc.cluster.local")
                .claim("roles", roleNames)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTokenValiditySeconds)))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTokenValiditySeconds)))
                .signWith(key)
                .compact();
    }

    // Claims 파싱 (한 번만 정의해두고 재사용)
    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰도 Claims 자체는 얻을 수 있음
            return e.getClaims();
        }
    }

    public Date getExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    public String getJti(String token) {
        return parseClaims(token).getId();
    }


    public boolean validateToken(String token) {

        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);;
            return true;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("Wrong Signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public Set<String> getRolesFromToken(String token) {

        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        Object roles = claims.get("roles");
        if (roles instanceof Collection<?> coll) {
            return coll.stream().map(Object::toString).collect(Collectors.toSet());
        }
        return Set.of();
    }

}

