package com.nexware.product.dto;

import java.util.UUID;

public record ProductSearchCriteria(
        String keyword,
        UUID categoryId,
        String sku,
        String barcode,
        Boolean active,
        Boolean trackSerialNumbers
) {
}
