package com.nexware.warehouse.event;

import java.time.Instant;
import java.util.UUID;

public record TransferCompletedEvent(
        UUID productId,
        UUID sourceWarehouseId,
        UUID destinationWarehouseId,
        int quantity,
        Instant occurredAt
) {
}
