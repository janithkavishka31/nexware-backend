package com.nexware.product.dto;

import java.util.UUID;

public record SupplierSummaryResponse(
        UUID id,
        String code,
        String displayName,
        boolean preferred
) {
}
