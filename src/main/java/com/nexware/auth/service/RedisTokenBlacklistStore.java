package com.nexware.auth.service;

import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "nexware.redis.enabled", havingValue = "true", matchIfMissing = true)
public class RedisTokenBlacklistStore implements TokenBlacklistStore {

    private static final String BLACKLIST_KEY_PREFIX = "auth:blacklist:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void blacklist(String tokenId, Instant expiresAt) {
        Duration ttl = Duration.between(Instant.now(), expiresAt);
        if (ttl.isPositive()) {
            redisTemplate.opsForValue().set(BLACKLIST_KEY_PREFIX + tokenId, "revoked", ttl);
        }
    }

    @Override
    public boolean isBlacklisted(String tokenId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_KEY_PREFIX + tokenId));
    }
}
