package com.nexware.user.service;

import com.nexware.shared.exception.BusinessRuleViolationException;
import com.nexware.shared.exception.ResourceNotFoundException;
import com.nexware.user.dto.UserResponse;
import com.nexware.user.dto.UserSearchCriteria;
import com.nexware.user.dto.UserUpdateRequest;
import com.nexware.user.entity.Role;
import com.nexware.user.entity.RoleName;
import com.nexware.user.entity.User;
import com.nexware.user.mapper.UserMapper;
import com.nexware.user.repository.RoleRepository;
import com.nexware.user.repository.UserRepository;
import com.nexware.user.repository.UserSpecifications;
import java.time.Clock;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final Clock clock = Clock.systemUTC();

    @Transactional(readOnly = true)
    public UserResponse get(UUID id) {
        return userRepository.findByIdAndDeletedAtIsNull(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> search(UserSearchCriteria criteria, Pageable pageable) {
        return userRepository.findAll(UserSpecifications.matching(criteria), pageable)
                .map(userMapper::toResponse);
    }

    @Transactional
    public UserResponse update(UUID id, UserUpdateRequest request) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        user.updateProfile(
                request.firstName().trim(),
                request.lastName().trim(),
                trimToNull(request.phoneNumber())
        );

        if (request.enabled() != null) {
            user.setEnabled(request.enabled());
        }
        if (request.accountLocked() != null) {
            user.setAccountLocked(request.accountLocked());
        }
        if (request.roles() != null) {
            if (request.roles().isEmpty()) {
                throw new BusinessRuleViolationException("User must have at least one role");
            }
            user.replaceRoles(resolveRoles(request.roles()));
        }

        return userMapper.toResponse(user);
    }

    @Transactional
    public void delete(UUID id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.softDelete(Instant.now(clock));
    }

    private Set<Role> resolveRoles(Set<RoleName> roleNames) {
        Set<Role> roles = new HashSet<>();
        for (RoleName roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new BusinessRuleViolationException("Role is not configured: " + roleName));
            roles.add(role);
        }
        return roles;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
