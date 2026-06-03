package com.nexware.user.repository;

import com.nexware.user.dto.UserSearchCriteria;
import com.nexware.user.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class UserSpecifications {

    private UserSpecifications() {
    }

    public static Specification<User> matching(UserSearchCriteria criteria) {
        return Specification.where(notDeleted())
                .and(keyword(criteria.keyword()))
                .and(enabled(criteria.enabled()))
                .and(role(criteria.role()));
    }

    private static Specification<User> notDeleted() {
        return (root, query, builder) -> builder.isNull(root.get("deletedAt"));
    }

    private static Specification<User> keyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        String pattern = "%" + keyword.trim().toLowerCase() + "%";
        return (root, query, builder) -> builder.or(
                builder.like(builder.lower(root.get("email")), pattern),
                builder.like(builder.lower(root.get("firstName")), pattern),
                builder.like(builder.lower(root.get("lastName")), pattern)
        );
    }

    private static Specification<User> enabled(Boolean enabled) {
        if (enabled == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("enabled"), enabled);
    }

    private static Specification<User> role(com.nexware.user.entity.RoleName role) {
        if (role == null) {
            return null;
        }
        return (root, query, builder) -> {
            var join = root.join("roles");
            return builder.equal(join.get("name"), role);
        };
    }
}
