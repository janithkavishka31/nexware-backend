package com.nexware.reservation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReservationCreateRequest(
        @NotNull UUID warehouseId,
        @NotNull UUID productId,
        @Min(1) int quantity,
        Integer expirationMinutes
) {
}
