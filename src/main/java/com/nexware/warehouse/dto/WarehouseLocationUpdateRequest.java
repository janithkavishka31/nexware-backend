package com.nexware.warehouse.dto;

import jakarta.validation.constraints.Min;

public record WarehouseLocationUpdateRequest(
        @Min(1)
        int capacityUnits,

        boolean active
) {
}
