package com.nexware.reservation.service;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "nexware.redis.enabled", havingValue = "true", matchIfMissing = true)
public class RedisReservationExpirationCache implements ReservationExpirationCache {

    private static final String RESERVATION_KEY_PREFIX = "reservation:expires:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void track(UUID reservationId, String reference, Duration ttl) {
        if (ttl.isPositive()) {
            redisTemplate.opsForValue().set(RESERVATION_KEY_PREFIX + reservationId, reference, ttl);
        }
    }

    @Override
    public void remove(UUID reservationId) {
        redisTemplate.delete(RESERVATION_KEY_PREFIX + reservationId);
    }
}
