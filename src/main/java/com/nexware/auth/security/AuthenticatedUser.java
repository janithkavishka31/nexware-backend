package com.nexware.auth.security;

import com.nexware.user.entity.Role;
import com.nexware.user.entity.User;
import java.util.Collection;
import java.util.UUID;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class AuthenticatedUser implements UserDetails {

    private final UUID id;
    private final String email;
    private final String password;
    private final boolean enabled;
    private final boolean accountLocked;
    private final Collection<? extends GrantedAuthority> authorities;

    private AuthenticatedUser(
            UUID id,
            String email,
            String password,
            boolean enabled,
            boolean accountLocked,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.accountLocked = accountLocked;
        this.authorities = authorities;
    }

    public static AuthenticatedUser from(User user) {
        return new AuthenticatedUser(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.isEnabled(),
                user.isAccountLocked(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .toList()
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
