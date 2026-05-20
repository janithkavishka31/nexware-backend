package com.nexware.shared.event;

import com.nexware.config.KafkaTopicConfig;
import com.nexware.inventory.event.InventoryUpdatedEvent;
import com.nexware.inventory.event.LowStockDetectedEvent;
import com.nexware.purchaseorder.event.PurchaseOrderApprovedEvent;
import com.nexware.reservation.event.StockReservedEvent;
import com.nexware.warehouse.event.TransferCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "nexware.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class DomainEventKafkaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @ApplicationModuleListener
    void publishInventoryUpdated(InventoryUpdatedEvent event) {
        kafkaTemplate.send(KafkaTopicConfig.INVENTORY_UPDATED, event.inventoryId().toString(), event);
    }

    @ApplicationModuleListener
    void publishLowStockDetected(LowStockDetectedEvent event) {
        kafkaTemplate.send(KafkaTopicConfig.LOW_STOCK_DETECTED, event.inventoryId().toString(), event);
    }

    @ApplicationModuleListener
    void publishStockReserved(StockReservedEvent event) {
        kafkaTemplate.send(KafkaTopicConfig.STOCK_RESERVED, event.reservationId().toString(), event);
    }

    @ApplicationModuleListener
    void publishTransferCompleted(TransferCompletedEvent event) {
        kafkaTemplate.send(KafkaTopicConfig.TRANSFER_COMPLETED, event.productId().toString(), event);
    }

    @ApplicationModuleListener
    void publishPurchaseOrderApproved(PurchaseOrderApprovedEvent event) {
        kafkaTemplate.send(KafkaTopicConfig.PURCHASE_ORDER_APPROVED, event.purchaseOrderId().toString(), event);
    }
}
