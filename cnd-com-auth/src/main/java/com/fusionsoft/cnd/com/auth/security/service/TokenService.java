package com.fusionsoft.cnd.com.auth.security.service;

import com.fusionsoft.cnd.com.auth.security.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    public String generateAndStoreRefreshToken(String username) {
        String refreshToken = jwtProvider.generateRefreshToken(username);
        String key = "refresh:" + username;
        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                Duration.ofSeconds(jwtProvider.getRefreshTokenValiditySeconds())
        );
        return refreshToken;
    }

    public boolean validateRefreshToken(String username, String refreshToken) {
        String key = "refresh:" + username;
        String stored = redisTemplate.opsForValue().get(key);
        return stored != null && stored.equals(refreshToken) && jwtProvider.validateToken(refreshToken);
    }

    public void deleteRefreshToken(String username) {
        redisTemplate.delete("refresh:" + username);
    }

    public void blacklistAccessToken(String jti, long expiresInSeconds) {
        String key = "blacklist:" + jti;
        redisTemplate.opsForValue().set(key, "true", Duration.ofSeconds(expiresInSeconds));
    }

    public boolean isBlacklisted(String jti) {
        String key = "blacklist:" + jti;
        return redisTemplate.hasKey(key);
    }
}

