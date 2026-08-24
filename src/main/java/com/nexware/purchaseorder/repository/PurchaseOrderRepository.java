package com.nexware.purchaseorder.repository;

import com.nexware.purchaseorder.entity.PurchaseOrder;
import com.nexware.purchaseorder.entity.PurchaseOrderStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {

    @EntityGraph(attributePaths = "supplier")
    Page<PurchaseOrder> findAllByStatus(PurchaseOrderStatus status, Pageable pageable);

    @EntityGraph(attributePaths = "supplier")
    Page<PurchaseOrder> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = "supplier")
    Optional<PurchaseOrder> findById(UUID id);
}
