package com.nexware.inventory.repository;

import com.nexware.inventory.entity.Inventory;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, UUID>, JpaSpecificationExecutor<Inventory> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select inventory
              from Inventory inventory
             where inventory.warehouse.id = :warehouseId
               and inventory.product.id = :productId
            """)
    Optional<Inventory> findForUpdate(@Param("warehouseId") UUID warehouseId, @Param("productId") UUID productId);

    @Query("""
            select coalesce(sum(inventory.quantityOnHand), 0)
              from Inventory inventory
             where inventory.warehouse.id = :warehouseId
            """)
    long sumQuantityOnHandByWarehouseId(@Param("warehouseId") UUID warehouseId);

    Optional<Inventory> findByWarehouseIdAndProductId(UUID warehouseId, UUID productId);

    @Query("""
            select coalesce(sum(inventory.quantityOnHand * inventory.averageUnitCost), 0)
              from Inventory inventory
            """)
    BigDecimal totalInventoryValue();

    @Query("""
            select inventory
              from Inventory inventory
              join fetch inventory.product product
              join fetch inventory.warehouse warehouse
             where product.lowStockThreshold > 0
               and (inventory.quantityOnHand - inventory.quantityReserved) <= product.lowStockThreshold
            """)
    java.util.List<Inventory> findLowStockInventory();
}
