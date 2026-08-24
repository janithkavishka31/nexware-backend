package com.nexware.reservation.event;

import java.time.Instant;
import java.util.UUID;

public record StockReservedEvent(UUID reservationId, UUID productId, UUID warehouseId, int quantity, Instant occurredAt) {
}
