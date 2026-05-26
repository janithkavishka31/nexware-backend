package com.nexware.auth.mapper;

import com.nexware.auth.dto.UserProfileResponse;
import com.nexware.user.entity.Role;
import com.nexware.user.entity.User;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "roles", expression = "java(toRoleNames(user.getRoles()))")
    UserProfileResponse toUserProfile(User user);

    default Set<String> toRoleNames(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .map(Enum::name)
                .collect(Collectors.toUnmodifiableSet());
    }
}
