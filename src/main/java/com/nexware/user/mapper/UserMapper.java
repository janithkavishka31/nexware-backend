package com.nexware.user.mapper;

import com.nexware.user.dto.UserResponse;
import com.nexware.user.entity.Role;
import com.nexware.user.entity.User;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", expression = "java(toRoleNames(user.getRoles()))")
    UserResponse toResponse(User user);

    default Set<String> toRoleNames(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toUnmodifiableSet());
    }
}
