package com.nexware.inventory.service;

import com.nexware.inventory.dto.InventoryResponse;
import com.nexware.inventory.dto.InventorySearchCriteria;
import com.nexware.inventory.dto.InventoryValuationResponse;
import com.nexware.inventory.dto.StockAdjustmentRequest;
import com.nexware.inventory.dto.StockMovementResponse;
import com.nexware.inventory.entity.Inventory;
import com.nexware.inventory.entity.StockMovement;
import com.nexware.inventory.event.InventoryUpdatedEvent;
import com.nexware.inventory.event.LowStockDetectedEvent;
import com.nexware.inventory.mapper.InventoryMapper;
import com.nexware.inventory.repository.InventoryRepository;
import com.nexware.inventory.repository.InventorySpecifications;
import com.nexware.inventory.repository.StockMovementRepository;
import com.nexware.product.entity.Product;
import com.nexware.product.repository.ProductRepository;
import com.nexware.shared.exception.BusinessRuleViolationException;
import com.nexware.shared.exception.ResourceNotFoundException;
import com.nexware.warehouse.entity.Warehouse;
import com.nexware.warehouse.entity.WarehouseLocation;
import com.nexware.warehouse.repository.WarehouseLocationRepository;
import com.nexware.warehouse.repository.WarehouseRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseLocationRepository locationRepository;
    private final InventoryMapper inventoryMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock = Clock.systemUTC();

    @Transactional(readOnly = true)
    public Page<InventoryResponse> searchInventory(InventorySearchCriteria criteria, Pageable pageable) {
        return inventoryRepository.findAll(InventorySpecifications.matching(criteria), pageable)
                .map(inventoryMapper::toInventoryResponse);
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventory(UUID warehouseId, UUID productId) {
        Inventory inventory = inventoryRepository.findByWarehouseIdAndProductId(warehouseId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory record", "%s:%s".formatted(warehouseId, productId)));
        return inventoryMapper.toInventoryResponse(inventory);
    }

    @Transactional
    public InventoryResponse adjustStock(StockAdjustmentRequest request) {
        Warehouse warehouse = requireActiveWarehouse(request.warehouseId());
        Product product = requireActiveProduct(request.productId());
        WarehouseLocation location = resolveLocation(request.warehouseId(), request.locationId());

        Inventory inventory = inventoryRepository.findForUpdate(warehouse.getId(), product.getId())
                .orElseGet(() -> inventoryRepository.save(Inventory.empty(
                        warehouse,
                        product,
                        location,
                        request.unitCost() == null ? product.getUnitCost() : request.unitCost()
                )));

        if (request.newQuantityOnHand() < inventory.getQuantityReserved()) {
            throw new BusinessRuleViolationException("Quantity on hand cannot be lower than reserved quantity");
        }

        int previousQuantity = inventory.getQuantityOnHand();
        int delta = request.newQuantityOnHand() - previousQuantity;
        BigDecimal unitCost = request.unitCost() == null ? inventory.getAverageUnitCost() : request.unitCost();
        inventory.adjustTo(request.newQuantityOnHand(), unitCost, location);

        Instant adjustedAt = Instant.now(clock);
        if (delta != 0) {
            stockMovementRepository.save(StockMovement.adjustment(
                    product,
                    warehouse,
                    delta,
                    unitCost,
                    trimToNull(request.reason()),
                    adjustedAt
            ));
        }

        publishInventoryEvents(inventory, adjustedAt);
        return inventoryMapper.toInventoryResponse(inventory);
    }

    @Transactional(readOnly = true)
    public Page<StockMovementResponse> getProductHistory(UUID productId, Pageable pageable) {
        requireActiveProduct(productId);
        return stockMovementRepository.findAllByProductIdOrderByOccurredAtDesc(productId, pageable)
                .map(inventoryMapper::toMovementResponse);
    }

    @Transactional(readOnly = true)
    public Page<StockMovementResponse> getWarehouseHistory(UUID warehouseId, Pageable pageable) {
        requireWarehouse(warehouseId);
        return stockMovementRepository
                .findAllBySourceWarehouseIdOrDestinationWarehouseIdOrderByOccurredAtDesc(warehouseId, warehouseId, pageable)
                .map(inventoryMapper::toMovementResponse);
    }

    @Transactional(readOnly = true)
    public Page<InventoryResponse> lowStock(Pageable pageable) {
        InventorySearchCriteria criteria = new InventorySearchCriteria(null, null, null, true);
        return inventoryRepository.findAll(InventorySpecifications.matching(criteria), pageable)
                .map(inventoryMapper::toInventoryResponse);
    }

    @Transactional(readOnly = true)
    public InventoryValuationResponse valuation() {
        return new InventoryValuationResponse(inventoryRepository.totalInventoryValue());
    }

    private void publishInventoryEvents(Inventory inventory, Instant occurredAt) {
        eventPublisher.publishEvent(new InventoryUpdatedEvent(
                inventory.getId(),
                inventory.getProduct().getId(),
                inventory.getWarehouse().getId(),
                inventory.getQuantityOnHand(),
                inventory.availableQuantity(),
                occurredAt
        ));

        int threshold = inventory.getProduct().getLowStockThreshold();
        if (threshold > 0 && inventory.availableQuantity() <= threshold) {
            eventPublisher.publishEvent(new LowStockDetectedEvent(
                    inventory.getId(),
                    inventory.getProduct().getId(),
                    inventory.getWarehouse().getId(),
                    inventory.availableQuantity(),
                    threshold,
                    occurredAt
            ));
        }
    }

    private Warehouse requireWarehouse(UUID warehouseId) {
        return warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", warehouseId));
    }

    private Warehouse requireActiveWarehouse(UUID warehouseId) {
        return warehouseRepository.findByIdAndActiveTrueAndDeletedAtIsNull(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Active warehouse", warehouseId));
    }

    private Product requireActiveProduct(UUID productId) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        if (!product.isActive()) {
            throw new BusinessRuleViolationException("Product is inactive");
        }
        return product;
    }

    private WarehouseLocation resolveLocation(UUID warehouseId, UUID locationId) {
        if (locationId == null) {
            return null;
        }
        return locationRepository.findByIdAndWarehouseId(locationId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse location", locationId));
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
