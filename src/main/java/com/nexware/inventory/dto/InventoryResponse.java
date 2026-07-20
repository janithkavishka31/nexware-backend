package com.nexware.inventory.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record InventoryResponse(
        UUID id,
        UUID warehouseId,
        String warehouseCode,
        UUID productId,
        String sku,
        String productName,
        UUID locationId,
        int quantityOnHand,
        int quantityReserved,
        int quantityAvailable,
        BigDecimal averageUnitCost,
        BigDecimal inventoryValue,
        boolean lowStock,
        Instant updatedAt,
        long version
) {
}
