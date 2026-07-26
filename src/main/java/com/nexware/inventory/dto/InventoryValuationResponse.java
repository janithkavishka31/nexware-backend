package com.nexware.inventory.dto;

import java.math.BigDecimal;

public record InventoryValuationResponse(
        BigDecimal totalValue
) {
}
