package com.nexware.inventory.event;

import java.time.Instant;
import java.util.UUID;

public record InventoryUpdatedEvent(
        UUID inventoryId,
        UUID productId,
        UUID warehouseId,
        int quantityOnHand,
        int quantityAvailable,
        Instant occurredAt
) {
}
