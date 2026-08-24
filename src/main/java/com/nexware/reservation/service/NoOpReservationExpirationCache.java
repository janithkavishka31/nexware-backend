package com.nexware.reservation.service;

import java.time.Duration;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "nexware.redis.enabled", havingValue = "false")
public class NoOpReservationExpirationCache implements ReservationExpirationCache {

    @Override
    public void track(UUID reservationId, String reference, Duration ttl) {
        // Expiration is handled by DB scheduler when Redis is disabled.
    }

    @Override
    public void remove(UUID reservationId) {
        // No-op
    }
}
