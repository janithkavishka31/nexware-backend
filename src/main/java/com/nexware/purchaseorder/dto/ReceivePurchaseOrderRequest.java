package com.nexware.purchaseorder.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReceivePurchaseOrderRequest(@NotNull UUID warehouseId) {
}
