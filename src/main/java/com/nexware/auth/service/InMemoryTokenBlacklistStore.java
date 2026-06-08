package com.nexware.auth.service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "nexware.redis.enabled", havingValue = "false")
public class InMemoryTokenBlacklistStore implements TokenBlacklistStore {

    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    @Override
    public void blacklist(String tokenId, Instant expiresAt) {
        blacklist.put(tokenId, expiresAt);
    }

    @Override
    public boolean isBlacklisted(String tokenId) {
        Instant expiresAt = blacklist.get(tokenId);
        if (expiresAt == null) {
            return false;
        }
        if (expiresAt.isBefore(Instant.now())) {
            blacklist.remove(tokenId);
            return false;
        }
        return true;
    }
}
