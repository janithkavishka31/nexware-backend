package com.nexware.warehouse.service;

import com.nexware.inventory.entity.Inventory;
import com.nexware.inventory.entity.StockMovement;
import com.nexware.inventory.repository.InventoryRepository;
import com.nexware.inventory.repository.StockMovementRepository;
import com.nexware.product.entity.Product;
import com.nexware.product.repository.ProductRepository;
import com.nexware.shared.exception.BusinessRuleViolationException;
import com.nexware.shared.exception.ConflictException;
import com.nexware.shared.exception.ResourceNotFoundException;
import com.nexware.warehouse.dto.WarehouseCapacityResponse;
import com.nexware.warehouse.dto.WarehouseCreateRequest;
import com.nexware.warehouse.dto.WarehouseLocationCreateRequest;
import com.nexware.warehouse.dto.WarehouseLocationResponse;
import com.nexware.warehouse.dto.WarehouseLocationUpdateRequest;
import com.nexware.warehouse.dto.WarehouseResponse;
import com.nexware.warehouse.dto.WarehouseSearchCriteria;
import com.nexware.warehouse.dto.WarehouseTransferRequest;
import com.nexware.warehouse.dto.WarehouseTransferResponse;
import com.nexware.warehouse.dto.WarehouseUpdateRequest;
import com.nexware.warehouse.entity.Warehouse;
import com.nexware.warehouse.entity.WarehouseLocation;
import com.nexware.warehouse.event.TransferCompletedEvent;
import com.nexware.warehouse.mapper.WarehouseMapper;
import com.nexware.warehouse.repository.WarehouseLocationRepository;
import com.nexware.warehouse.repository.WarehouseRepository;
import com.nexware.warehouse.repository.WarehouseSpecifications;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseLocationRepository locationRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final WarehouseMapper warehouseMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock = Clock.systemUTC();

    @Transactional
    public WarehouseResponse createWarehouse(WarehouseCreateRequest request) {
        String code = normalizeCode(request.code());
        warehouseRepository.findByCodeAndDeletedAtIsNull(code)
                .ifPresent(existing -> {
                    throw new ConflictException("Warehouse code already exists");
                });

        Warehouse warehouse = Warehouse.create(
                code,
                request.name().trim(),
                request.addressLine1().trim(),
                trimToNull(request.addressLine2()),
                request.city().trim(),
                trimToNull(request.state()),
                trimToNull(request.postalCode()),
                request.country().trim(),
                request.capacityUnits()
        );

        return warehouseMapper.toWarehouseResponse(warehouseRepository.save(warehouse));
    }

    @Transactional(readOnly = true)
    public WarehouseResponse getWarehouse(UUID warehouseId) {
        return warehouseMapper.toWarehouseResponse(requireWarehouse(warehouseId));
    }

    @Transactional(readOnly = true)
    public Page<WarehouseResponse> searchWarehouses(WarehouseSearchCriteria criteria, Pageable pageable) {
        return warehouseRepository.findAll(WarehouseSpecifications.matching(criteria), pageable)
                .map(warehouseMapper::toWarehouseResponse);
    }

    @Transactional
    public WarehouseResponse updateWarehouse(UUID warehouseId, WarehouseUpdateRequest request) {
        Warehouse warehouse = requireWarehouse(warehouseId);
        long usedUnits = inventoryRepository.sumQuantityOnHandByWarehouseId(warehouseId);
        if (request.capacityUnits() < usedUnits) {
            throw new BusinessRuleViolationException("Warehouse capacity cannot be lower than current stock on hand");
        }

        warehouse.update(
                request.name().trim(),
                request.addressLine1().trim(),
                trimToNull(request.addressLine2()),
                request.city().trim(),
                trimToNull(request.state()),
                trimToNull(request.postalCode()),
                request.country().trim(),
                request.capacityUnits(),
                request.active()
        );
        return warehouseMapper.toWarehouseResponse(warehouse);
    }

    @Transactional
    public void deleteWarehouse(UUID warehouseId) {
        Warehouse warehouse = requireWarehouse(warehouseId);
        long usedUnits = inventoryRepository.sumQuantityOnHandByWarehouseId(warehouseId);
        if (usedUnits > 0) {
            throw new BusinessRuleViolationException("Warehouse with stock on hand cannot be deleted");
        }
        warehouse.softDelete(Instant.now(clock));
    }

    @Transactional
    public WarehouseLocationResponse createLocation(UUID warehouseId, WarehouseLocationCreateRequest request) {
        Warehouse warehouse = requireActiveWarehouse(warehouseId);
        String zone = normalizeSlot(request.zone());
        String aisle = normalizeSlot(request.aisle());
        String rack = normalizeSlot(request.rack());
        String bin = normalizeSlot(request.bin());

        if (locationRepository.existsByWarehouseIdAndZoneAndAisleAndRackAndBin(warehouseId, zone, aisle, rack, bin)) {
            throw new ConflictException("Warehouse location already exists");
        }

        WarehouseLocation location = WarehouseLocation.create(warehouse, zone, aisle, rack, bin, request.capacityUnits());
        return warehouseMapper.toLocationResponse(locationRepository.save(location));
    }

    @Transactional(readOnly = true)
    public List<WarehouseLocationResponse> listLocations(UUID warehouseId) {
        requireWarehouse(warehouseId);
        return locationRepository.findAllByWarehouseIdOrderByZoneAscAisleAscRackAscBinAsc(warehouseId).stream()
                .map(warehouseMapper::toLocationResponse)
                .toList();
    }

    @Transactional
    public WarehouseLocationResponse updateLocation(
            UUID warehouseId,
            UUID locationId,
            WarehouseLocationUpdateRequest request
    ) {
        requireWarehouse(warehouseId);
        WarehouseLocation location = locationRepository.findByIdAndWarehouseId(locationId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse location", locationId));
        location.update(request.capacityUnits(), request.active());
        return warehouseMapper.toLocationResponse(location);
    }

    @Transactional(readOnly = true)
    public WarehouseCapacityResponse getCapacity(UUID warehouseId) {
        Warehouse warehouse = requireWarehouse(warehouseId);
        return capacityResponse(warehouse, inventoryRepository.sumQuantityOnHandByWarehouseId(warehouseId));
    }

    @Transactional
    public WarehouseTransferResponse transferStock(WarehouseTransferRequest request) {
        if (request.sourceWarehouseId().equals(request.destinationWarehouseId())) {
            throw new BusinessRuleViolationException("Source and destination warehouses must be different");
        }

        Warehouse sourceWarehouse = requireActiveWarehouse(request.sourceWarehouseId());
        Warehouse destinationWarehouse = requireActiveWarehouse(request.destinationWarehouseId());
        Product product = requireActiveProduct(request.productId());
        ensureDestinationCapacity(destinationWarehouse, request.quantity());

        Inventory sourceInventory = inventoryRepository
                .findForUpdate(sourceWarehouse.getId(), product.getId())
                .orElseThrow(() -> new BusinessRuleViolationException("Source warehouse has no inventory for this product"));

        if (sourceInventory.availableQuantity() < request.quantity()) {
            throw new BusinessRuleViolationException("Insufficient available stock in source warehouse");
        }

        Inventory destinationInventory = inventoryRepository
                .findForUpdate(destinationWarehouse.getId(), product.getId())
                .orElseGet(() -> inventoryRepository.save(
                        Inventory.empty(destinationWarehouse, product, sourceInventory.getAverageUnitCost())
                ));

        Instant transferredAt = Instant.now(clock);
        sourceInventory.transferOut(request.quantity());
        destinationInventory.transferIn(request.quantity(), sourceInventory.getAverageUnitCost());

        stockMovementRepository.saveAll(List.of(
                StockMovement.transferOut(
                        product,
                        sourceWarehouse,
                        destinationWarehouse,
                        request.quantity(),
                        sourceInventory.getAverageUnitCost(),
                        trimToNull(request.reason()),
                        transferredAt
                ),
                StockMovement.transferIn(
                        product,
                        sourceWarehouse,
                        destinationWarehouse,
                        request.quantity(),
                        sourceInventory.getAverageUnitCost(),
                        trimToNull(request.reason()),
                        transferredAt
                )
        ));

        eventPublisher.publishEvent(new TransferCompletedEvent(
                product.getId(),
                sourceWarehouse.getId(),
                destinationWarehouse.getId(),
                request.quantity(),
                transferredAt
        ));

        return new WarehouseTransferResponse(
                product.getId(),
                product.getSku(),
                sourceWarehouse.getId(),
                destinationWarehouse.getId(),
                request.quantity(),
                sourceInventory.availableQuantity(),
                destinationInventory.getQuantityOnHand(),
                transferredAt
        );
    }

    private void ensureDestinationCapacity(Warehouse destinationWarehouse, int transferQuantity) {
        WarehouseCapacityResponse capacity = capacityResponse(
                destinationWarehouse,
                inventoryRepository.sumQuantityOnHandByWarehouseId(destinationWarehouse.getId())
        );
        if (capacity.availableUnits() < transferQuantity) {
            throw new BusinessRuleViolationException("Destination warehouse does not have enough remaining capacity");
        }
    }

    private WarehouseCapacityResponse capacityResponse(Warehouse warehouse, long usedUnits) {
        long availableUnits = Math.max(0, warehouse.getCapacityUnits() - usedUnits);
        BigDecimal utilization = warehouse.getCapacityUnits() == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(usedUnits)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(warehouse.getCapacityUnits()), 2, RoundingMode.HALF_UP);
        return new WarehouseCapacityResponse(
                warehouse.getId(),
                warehouse.getCode(),
                warehouse.getCapacityUnits(),
                usedUnits,
                availableUnits,
                utilization
        );
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

    private String normalizeCode(String value) {
        return value.trim().toUpperCase();
    }

    private String normalizeSlot(String value) {
        return value.trim().toUpperCase();
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
