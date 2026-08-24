package com.nexware.purchaseorder.event;

import java.time.Instant;
import java.util.UUID;

public record PurchaseOrderApprovedEvent(UUID purchaseOrderId, UUID supplierId, Instant occurredAt) {
}
