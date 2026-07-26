package com.nexware.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record StockAdjustmentRequest(
        @NotNull
        UUID warehouseId,

        @NotNull
        UUID productId,

        UUID locationId,

        @Min(0)
        int newQuantityOnHand,

        @DecimalMin("0.00")
        BigDecimal unitCost,

        @Size(max = 500)
        String reason
) {
}
