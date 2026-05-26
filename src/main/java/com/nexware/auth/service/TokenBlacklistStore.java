package com.nexware.auth.service;

import java.time.Instant;

public interface TokenBlacklistStore {

    void blacklist(String tokenId, Instant expiresAt);

    boolean isBlacklisted(String tokenId);
}
