package com.nexware.inventory.dto;

import com.nexware.inventory.entity.StockMovementType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record StockMovementResponse(
        UUID id,
        UUID productId,
        String sku,
        UUID sourceWarehouseId,
        UUID destinationWarehouseId,
        StockMovementType movementType,
        int quantity,
        BigDecimal unitCost,
        String referenceType,
        UUID referenceId,
        String reason,
        Instant occurredAt
) {
}
