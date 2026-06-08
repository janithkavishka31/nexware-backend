package com.nexware.user.dto;

import com.nexware.user.entity.RoleName;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public record UserUpdateRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String phoneNumber,
        Boolean enabled,
        Boolean accountLocked,
        Set<RoleName> roles
) {
}
