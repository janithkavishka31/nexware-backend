package com.nexware.auth.service;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistStore tokenBlacklistStore;

    public void blacklist(String tokenId, Instant expiresAt) {
        tokenBlacklistStore.blacklist(tokenId, expiresAt);
    }

    public boolean isBlacklisted(String tokenId) {
        return tokenBlacklistStore.isBlacklisted(tokenId);
    }
}
