package com.nexware.notification.service;

import com.nexware.inventory.event.LowStockDetectedEvent;
import com.nexware.notification.dto.NotificationResponse;
import com.nexware.notification.entity.Notification;
import com.nexware.notification.entity.NotificationType;
import com.nexware.notification.mapper.NotificationMapper;
import com.nexware.notification.repository.NotificationRepository;
import com.nexware.purchaseorder.event.PurchaseOrderApprovedEvent;
import com.nexware.reservation.event.StockReservedEvent;
import com.nexware.warehouse.event.TransferCompletedEvent;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationMapper mapper;
    private final Clock clock = Clock.systemUTC();

    public Page<NotificationResponse> list(Pageable pageable) {
        return repository.findAllByOrderByCreatedAtDesc(pageable).map(mapper::toResponse);
    }

    @ApplicationModuleListener
    void onLowStock(LowStockDetectedEvent event) {
        create(NotificationType.LOW_STOCK, "Low stock detected", "Product inventory has reached its low stock threshold", event.toString());
    }

    @ApplicationModuleListener
    void onTransfer(TransferCompletedEvent event) {
        create(NotificationType.TRANSFER, "Transfer completed", "Warehouse stock transfer completed", event.toString());
    }

    @ApplicationModuleListener
    void onReservation(StockReservedEvent event) {
        create(NotificationType.RESERVATION, "Stock reserved", "Inventory reservation was created", event.toString());
    }

    @ApplicationModuleListener
    void onPurchaseOrderApproved(PurchaseOrderApprovedEvent event) {
        create(NotificationType.PURCHASE_ORDER, "Purchase order approved", "Purchase order is ready for receiving", event.toString());
    }

    private void create(NotificationType type, String title, String message, String payload) {
        Instant now = Instant.now(clock);
        repository.save(Notification.create(type, title, message, "{\"event\":\"" + payload.replace("\"", "'") + "\"}", now));
    }
}
