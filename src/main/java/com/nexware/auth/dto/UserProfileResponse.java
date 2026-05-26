package com.nexware.auth.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        Set<String> roles,
        Instant lastLoginAt
) {
}
