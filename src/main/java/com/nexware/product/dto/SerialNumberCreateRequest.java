package com.nexware.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record SerialNumberCreateRequest(
        @NotBlank
        @Size(max = 160)
        String serialNumber,

        UUID warehouseId
) {
}
