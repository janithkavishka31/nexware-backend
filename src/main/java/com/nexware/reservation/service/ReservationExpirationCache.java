package com.nexware.reservation.service;

import java.time.Duration;
import java.util.UUID;

public interface ReservationExpirationCache {

    void track(UUID reservationId, String reference, Duration ttl);

    void remove(UUID reservationId);
}
