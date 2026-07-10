package com.nexware.warehouse.repository;

import com.nexware.warehouse.entity.WarehouseLocation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseLocationRepository extends JpaRepository<WarehouseLocation, UUID> {

    List<WarehouseLocation> findAllByWarehouseIdOrderByZoneAscAisleAscRackAscBinAsc(UUID warehouseId);

    Optional<WarehouseLocation> findByIdAndWarehouseId(UUID id, UUID warehouseId);

    boolean existsByWarehouseIdAndZoneAndAisleAndRackAndBin(UUID warehouseId, String zone, String aisle, String rack, String bin);
}
