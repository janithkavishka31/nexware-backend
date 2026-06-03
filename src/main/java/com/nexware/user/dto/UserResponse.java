package com.nexware.user.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        boolean enabled,
        boolean accountLocked,
        Set<String> roles,
        Instant lastLoginAt,
        Instant createdAt,
        Instant updatedAt
) {
}
