package com.nexware.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record ProductCategoryCreateRequest(
        @NotBlank
        @Size(max = 40)
        String code,

        @NotBlank
        @Size(max = 140)
        String name,

        @Size(max = 500)
        String description,

        UUID parentId
) {
}
