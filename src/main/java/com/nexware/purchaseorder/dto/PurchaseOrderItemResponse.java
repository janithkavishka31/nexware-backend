package com.nexware.purchaseorder.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PurchaseOrderItemResponse(UUID id, UUID productId, String sku, int orderedQuantity, int receivedQuantity, BigDecimal unitCost, BigDecimal lineTotal) {
}
