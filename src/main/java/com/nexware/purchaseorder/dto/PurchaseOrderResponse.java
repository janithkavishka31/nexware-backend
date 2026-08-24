package com.nexware.purchaseorder.dto;

import com.nexware.purchaseorder.entity.PurchaseOrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PurchaseOrderResponse(
        UUID id,
        UUID supplierId,
        String supplierName,
        String orderNumber,
        PurchaseOrderStatus status,
        LocalDate expectedDeliveryDate,
        String rejectedReason,
        String notes,
        BigDecimal subtotal,
        BigDecimal taxTotal,
        BigDecimal grandTotal,
        Instant approvedAt,
        Instant receivedAt,
        List<PurchaseOrderItemResponse> items
) {
}
