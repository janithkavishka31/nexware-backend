package com.nexware.warehouse.dto;

import java.time.Instant;
import java.util.UUID;

public record WarehouseResponse(
        UUID id,
        String code,
        String name,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        String country,
        int capacityUnits,
        boolean active,
        Instant createdAt,
        Instant updatedAt,
        long version
) {
}
