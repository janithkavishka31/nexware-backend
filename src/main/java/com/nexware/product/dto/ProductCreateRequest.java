package com.nexware.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record ProductCreateRequest(
        @NotNull
        UUID categoryId,

        @NotBlank
        @Size(max = 80)
        String sku,

        @Size(max = 128)
        String barcode,

        @NotBlank
        @Size(max = 180)
        String name,

        String description,

        @Size(max = 120)
        String brand,

        @Size(max = 120)
        String model,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal unitCost,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal unitPrice,

        @Min(0)
        int lowStockThreshold,

        boolean trackSerialNumbers,

        Set<UUID> supplierIds
) {
}
