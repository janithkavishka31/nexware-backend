package com.nexware.purchaseorder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectPurchaseOrderRequest(@NotBlank @Size(max = 500) String reason) {
}
