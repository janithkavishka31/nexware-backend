package com.nexware.inventory.dto;

import java.util.UUID;

public record InventorySearchCriteria(
        UUID warehouseId,
        UUID productId,
        String sku,
        Boolean lowStockOnly
) {
}
