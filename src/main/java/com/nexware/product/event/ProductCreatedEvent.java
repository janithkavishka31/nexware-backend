package com.nexware.product.event;

import java.time.Instant;
import java.util.UUID;

public record ProductCreatedEvent(
        UUID productId,
        String sku,
        Instant occurredAt
) {
}
