package com.nexware.user.repository;

import com.nexware.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByIdAndDeletedAtIsNull(UUID id);

    boolean existsByEmailAndDeletedAtIsNull(String email);
}
