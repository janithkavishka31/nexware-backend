package com.nexware.inventory.repository;

import com.nexware.inventory.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    Page<StockMovement> findAllByProductIdOrderByOccurredAtDesc(UUID productId, Pageable pageable);

    Page<StockMovement> findAllBySourceWarehouseIdOrDestinationWarehouseIdOrderByOccurredAtDesc(
            UUID sourceWarehouseId,
            UUID destinationWarehouseId,
            Pageable pageable
    );
}
