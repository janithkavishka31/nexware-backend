package com.nexware.purchaseorder.repository;

import com.nexware.purchaseorder.entity.PurchaseOrderItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, UUID> {

    @EntityGraph(attributePaths = "product")
    List<PurchaseOrderItem> findAllByPurchaseOrderId(UUID purchaseOrderId);
}
