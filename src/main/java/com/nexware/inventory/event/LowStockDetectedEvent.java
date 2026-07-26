package com.nexware.inventory.event;

import java.time.Instant;
import java.util.UUID;

public record LowStockDetectedEvent(
        UUID inventoryId,
        UUID productId,
        UUID warehouseId,
        int quantityAvailable,
        int lowStockThreshold,
        Instant occurredAt
) {
}
