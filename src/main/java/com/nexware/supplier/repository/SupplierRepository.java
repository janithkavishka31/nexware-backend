package com.nexware.supplier.repository;

import com.nexware.supplier.entity.Supplier;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SupplierRepository extends JpaRepository<Supplier, UUID>, JpaSpecificationExecutor<Supplier> {

    List<Supplier> findAllByIdInAndDeletedAtIsNull(List<UUID> ids);

    Optional<Supplier> findByIdAndDeletedAtIsNull(UUID id);

    Optional<Supplier> findByIdAndActiveTrueAndDeletedAtIsNull(UUID id);

    Optional<Supplier> findByCodeAndDeletedAtIsNull(String code);
}
