package com.nexware.purchaseorder.service;

import com.nexware.auth.security.AuthenticatedUser;
import com.nexware.inventory.entity.Inventory;
import com.nexware.inventory.entity.StockMovement;
import com.nexware.inventory.repository.InventoryRepository;
import com.nexware.inventory.repository.StockMovementRepository;
import com.nexware.product.entity.Product;
import com.nexware.product.repository.ProductRepository;
import com.nexware.purchaseorder.dto.PurchaseOrderCreateRequest;
import com.nexware.purchaseorder.dto.PurchaseOrderResponse;
import com.nexware.purchaseorder.dto.ReceivePurchaseOrderRequest;
import com.nexware.purchaseorder.dto.RejectPurchaseOrderRequest;
import com.nexware.purchaseorder.entity.PurchaseOrder;
import com.nexware.purchaseorder.entity.PurchaseOrderItem;
import com.nexware.purchaseorder.entity.PurchaseOrderStatus;
import com.nexware.purchaseorder.event.PurchaseOrderApprovedEvent;
import com.nexware.purchaseorder.mapper.PurchaseOrderMapper;
import com.nexware.purchaseorder.repository.PurchaseOrderItemRepository;
import com.nexware.purchaseorder.repository.PurchaseOrderRepository;
import com.nexware.shared.exception.BusinessRuleViolationException;
import com.nexware.shared.exception.ResourceNotFoundException;
import com.nexware.supplier.entity.Supplier;
import com.nexware.supplier.repository.SupplierRepository;
import com.nexware.user.entity.User;
import com.nexware.user.repository.UserRepository;
import com.nexware.warehouse.entity.Warehouse;
import com.nexware.warehouse.repository.WarehouseRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository orderRepository;
    private final PurchaseOrderItemRepository itemRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final PurchaseOrderMapper mapper;
    private final ApplicationEventPublisher events;
    private final Clock clock = Clock.systemUTC();

    @Transactional
    public PurchaseOrderResponse create(PurchaseOrderCreateRequest request, AuthenticatedUser principal) {
        Supplier supplier = supplierRepository.findByIdAndActiveTrueAndDeletedAtIsNull(request.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Active supplier", request.supplierId()));
        User creator = userRepository.findByIdAndDeletedAtIsNull(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", principal.getId()));
        PurchaseOrder order = orderRepository.save(PurchaseOrder.draft(supplier, "PO-" + Instant.now(clock).toEpochMilli(), request.expectedDeliveryDate(), request.notes(), creator));
        List<PurchaseOrderItem> items = request.items().stream().map(item -> {
            Product product = productRepository.findByIdAndDeletedAtIsNull(item.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", item.productId()));
            return PurchaseOrderItem.create(order, product, item.quantity(), item.unitCost());
        }).toList();
        itemRepository.saveAll(items);
        BigDecimal subtotal = items.stream().map(PurchaseOrderItem::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        order.updateTotals(subtotal, BigDecimal.ZERO);
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<PurchaseOrderResponse> list(PurchaseOrderStatus status, Pageable pageable) {
        Page<PurchaseOrder> page = status == null ? orderRepository.findAllBy(pageable) : orderRepository.findAllByStatus(status, pageable);
        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PurchaseOrderResponse get(UUID id) {
        return toResponse(requireOrder(id));
    }

    @Transactional
    public PurchaseOrderResponse approve(UUID id, AuthenticatedUser principal) {
        PurchaseOrder order = requireOrder(id);
        if (order.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new BusinessRuleViolationException("Only draft purchase orders can be approved");
        }
        User approver = userRepository.findByIdAndDeletedAtIsNull(principal.getId()).orElseThrow(() -> new ResourceNotFoundException("User", principal.getId()));
        Instant now = Instant.now(clock);
        order.approve(approver, now);
        events.publishEvent(new PurchaseOrderApprovedEvent(order.getId(), order.getSupplier().getId(), now));
        return toResponse(order);
    }

    @Transactional
    public PurchaseOrderResponse reject(UUID id, RejectPurchaseOrderRequest request) {
        PurchaseOrder order = requireOrder(id);
        if (order.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new BusinessRuleViolationException("Only draft purchase orders can be rejected");
        }
        order.reject(request.reason());
        return toResponse(order);
    }

    @Transactional
    public PurchaseOrderResponse receive(UUID id, ReceivePurchaseOrderRequest request) {
        PurchaseOrder order = requireOrder(id);
        if (order.getStatus() != PurchaseOrderStatus.APPROVED) {
            throw new BusinessRuleViolationException("Only approved purchase orders can be received");
        }
        Warehouse warehouse = warehouseRepository.findByIdAndActiveTrueAndDeletedAtIsNull(request.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Active warehouse", request.warehouseId()));
        Instant now = Instant.now(clock);
        for (PurchaseOrderItem item : itemRepository.findAllByPurchaseOrderId(order.getId())) {
            Inventory inventory = inventoryRepository.findForUpdate(warehouse.getId(), item.getProduct().getId())
                    .orElseGet(() -> inventoryRepository.save(Inventory.empty(warehouse, item.getProduct(), item.getUnitCost())));
            inventory.receive(item.getOrderedQuantity(), item.getUnitCost());
            item.markReceived();
            stockMovementRepository.save(StockMovement.purchaseReceipt(item.getProduct(), warehouse, item.getOrderedQuantity(), item.getUnitCost(), order.getId(), now));
        }
        order.receive(now);
        return toResponse(order);
    }

    private PurchaseOrder requireOrder(UUID id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Purchase order", id));
    }

    private PurchaseOrderResponse toResponse(PurchaseOrder order) {
        return mapper.toResponse(order, itemRepository.findAllByPurchaseOrderId(order.getId()).stream().map(mapper::toItemResponse).toList());
    }
}
