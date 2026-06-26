package com.nexware.product.dto;

import java.util.UUID;

public record ProductSerialNumberResponse(
        UUID id,
        String serialNumber,
        UUID warehouseId,
        String inventoryStatus
) {
}
