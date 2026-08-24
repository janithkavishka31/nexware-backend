package com.nexware.purchaseorder.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record PurchaseOrderItemRequest(@NotNull UUID productId, @Min(1) int quantity, @NotNull @DecimalMin("0.00") BigDecimal unitCost) {
}
