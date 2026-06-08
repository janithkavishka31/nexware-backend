package com.nexware.user.controller;

import com.nexware.shared.api.ApiResponse;
import com.nexware.shared.api.PageResponse;
import com.nexware.user.dto.UserResponse;
import com.nexware.user.dto.UserSearchCriteria;
import com.nexware.user.dto.UserUpdateRequest;
import com.nexware.user.entity.RoleName;
import com.nexware.user.service.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${nexware.api.base-path}/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(userService.get(id));
    }

    @GetMapping
    public ApiResponse<PageResponse<UserResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) RoleName role,
            @PageableDefault(size = 20, sort = "email") Pageable pageable
    ) {
        return ApiResponse.success(PageResponse.from(
                userService.search(new UserSearchCriteria(keyword, enabled, role), pageable)));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> update(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.success("User updated successfully", userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
}
