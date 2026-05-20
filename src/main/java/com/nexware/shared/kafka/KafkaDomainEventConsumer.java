package com.nexware.shared.kafka;

import com.nexware.config.KafkaTopicConfig;
import com.nexware.inventory.event.InventoryUpdatedEvent;
import com.nexware.inventory.event.LowStockDetectedEvent;
import com.nexware.purchaseorder.event.PurchaseOrderApprovedEvent;
import com.nexware.reservation.event.StockReservedEvent;
import com.nexware.warehouse.event.TransferCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "nexware.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaDomainEventConsumer {

    @KafkaListener(topics = KafkaTopicConfig.INVENTORY_UPDATED, groupId = "nexware-inventory-updated")
    public void onInventoryUpdated(InventoryUpdatedEvent event) {
        log.info("Kafka consumed InventoryUpdatedEvent inventoryId={} warehouseId={} onHand={}",
                event.inventoryId(), event.warehouseId(), event.quantityOnHand());
    }

    @KafkaListener(topics = KafkaTopicConfig.LOW_STOCK_DETECTED, groupId = "nexware-low-stock")
    public void onLowStock(LowStockDetectedEvent event) {
        log.info("Kafka consumed LowStockDetectedEvent productId={} warehouseId={} available={}",
                event.productId(), event.warehouseId(), event.quantityAvailable());
    }

    @KafkaListener(topics = KafkaTopicConfig.STOCK_RESERVED, groupId = "nexware-stock-reserved")
    public void onStockReserved(StockReservedEvent event) {
        log.info("Kafka consumed StockReservedEvent reservationId={} quantity={}",
                event.reservationId(), event.quantity());
    }

    @KafkaListener(topics = KafkaTopicConfig.TRANSFER_COMPLETED, groupId = "nexware-transfer-completed")
    public void onTransferCompleted(TransferCompletedEvent event) {
        log.info("Kafka consumed TransferCompletedEvent productId={} from={} to={} qty={}",
                event.productId(), event.sourceWarehouseId(), event.destinationWarehouseId(), event.quantity());
    }

    @KafkaListener(topics = KafkaTopicConfig.PURCHASE_ORDER_APPROVED, groupId = "nexware-po-approved")
    public void onPurchaseOrderApproved(PurchaseOrderApprovedEvent event) {
        log.info("Kafka consumed PurchaseOrderApprovedEvent purchaseOrderId={} supplierId={}",
                event.purchaseOrderId(), event.supplierId());
    }
}
