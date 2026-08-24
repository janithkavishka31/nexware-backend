package com.nexware.reservation.dto;

import com.nexware.reservation.entity.ReservationStatus;
import java.time.Instant;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        String reservationReference,
        UUID inventoryId,
        UUID warehouseId,
        UUID productId,
        String sku,
        int quantity,
        ReservationStatus status,
        Instant expiresAt,
        Instant releasedAt
) {
}
