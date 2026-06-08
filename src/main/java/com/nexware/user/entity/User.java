package com.nexware.user.entity;

import com.nexware.shared.persistence.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "phone_number", length = 32)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "account_locked", nullable = false)
    private boolean accountLocked;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public static User register(
            String email,
            String passwordHash,
            String firstName,
            String lastName,
            String phoneNumber,
            Set<Role> roles
    ) {
        User user = new User();
        user.email = email.toLowerCase();
        user.passwordHash = passwordHash;
        user.firstName = firstName;
        user.lastName = lastName;
        user.phoneNumber = phoneNumber;
        user.roles = new HashSet<>(roles);
        user.enabled = true;
        user.accountLocked = false;
        return user;
    }

    public void recordSuccessfulLogin(Instant loggedInAt) {
        this.lastLoginAt = loggedInAt;
    }

    public void updateProfile(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public void replaceRoles(Set<Role> roles) {
        this.roles.clear();
        this.roles.addAll(roles);
    }

    public void softDelete(Instant deletedAt) {
        markDeleted(deletedAt);
        this.enabled = false;
    }
}
