package com.nexware.warehouse.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record WarehouseCapacityResponse(
        UUID warehouseId,
        String warehouseCode,
        int capacityUnits,
        long usedUnits,
        long availableUnits,
        BigDecimal utilizationPercentage
) {
}
