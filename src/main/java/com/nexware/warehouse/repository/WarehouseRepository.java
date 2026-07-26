package com.nexware.warehouse.repository;

import com.nexware.warehouse.entity.Warehouse;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WarehouseRepository extends JpaRepository<Warehouse, UUID>, JpaSpecificationExecutor<Warehouse> {

    Optional<Warehouse> findByIdAndDeletedAtIsNull(UUID id);

    Optional<Warehouse> findByIdAndActiveTrueAndDeletedAtIsNull(UUID id);

    Optional<Warehouse> findByCodeAndDeletedAtIsNull(String code);
}
