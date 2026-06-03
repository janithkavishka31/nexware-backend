package com.nexware.user.dto;

import com.nexware.user.entity.RoleName;

public record UserSearchCriteria(
        String keyword,
        Boolean enabled,
        RoleName role
) {
}
