package com.fusionsoft.cnd.com.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RedisTemplate<String, String> redisTemplate;

    // AccessToken 블랙리스트 등록
    public void blacklistAccessToken(String jti, long ttlSeconds) {
        redisTemplate.opsForValue().set("blacklist:" + jti, "true", ttlSeconds, TimeUnit.SECONDS);
    }

    // 블랙리스트 여부 확인
    public boolean isBlacklisted(String jti) {
        return redisTemplate.hasKey("blacklist:" + jti);
    }

    // RefreshToken 삭제
    public void deleteRefreshToken(String username) {
        redisTemplate.delete("refresh:" + username);
    }

    // RefreshToken 검증
    public boolean validateRefreshToken(String username, String token) {
        String storedToken = redisTemplate.opsForValue().get("refresh:" + username);
        return storedToken != null && storedToken.equals(token);
    }

    // RefreshToken 등록
    public void storeRefreshToken(String username, String token, long ttlSeconds) {
        redisTemplate.opsForValue().set("refresh:" + username, token, ttlSeconds, TimeUnit.SECONDS);
    }
}
