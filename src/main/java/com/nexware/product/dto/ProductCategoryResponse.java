package com.nexware.product.dto;

import java.util.UUID;

public record ProductCategoryResponse(
        UUID id,
        UUID parentId,
        String code,
        String name,
        String description,
        boolean active
) {
}
