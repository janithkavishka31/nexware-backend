package com.nexware.product.event;

import java.time.Instant;
import java.util.UUID;

public record ProductUpdatedEvent(
        UUID productId,
        String sku,
        Instant occurredAt
) {
}
