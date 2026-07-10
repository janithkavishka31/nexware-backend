package com.nexware.warehouse.dto;

import java.time.Instant;
import java.util.UUID;

public record WarehouseLocationResponse(
        UUID id,
        UUID warehouseId,
        String zone,
        String aisle,
        String rack,
        String bin,
        int capacityUnits,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
