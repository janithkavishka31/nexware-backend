package com.nexware.product.event;

import java.time.Instant;
import java.util.UUID;

public record ProductDeletedEvent(
        UUID productId,
        String sku,
        Instant occurredAt
) {
}
