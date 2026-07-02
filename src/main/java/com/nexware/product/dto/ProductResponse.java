package com.nexware.product.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        ProductCategoryResponse category,
        String sku,
        String barcode,
        String name,
        String description,
        String brand,
        String model,
        BigDecimal unitCost,
        BigDecimal unitPrice,
        int lowStockThreshold,
        boolean trackSerialNumbers,
        boolean active,
        List<SupplierSummaryResponse> suppliers,
        Instant createdAt,
        Instant updatedAt,
        long version
) {
}
