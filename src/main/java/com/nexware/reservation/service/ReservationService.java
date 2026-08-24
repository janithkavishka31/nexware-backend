package com.nexware.reservation.service;

import com.nexware.auth.security.AuthenticatedUser;
import com.nexware.config.ApplicationProperties;
import com.nexware.inventory.entity.Inventory;
import com.nexware.inventory.entity.StockMovement;
import com.nexware.inventory.entity.StockMovementType;
import com.nexware.inventory.repository.InventoryRepository;
import com.nexware.inventory.repository.StockMovementRepository;
import com.nexware.reservation.dto.ReservationCreateRequest;
import com.nexware.reservation.dto.ReservationResponse;
import com.nexware.reservation.entity.InventoryReservation;
import com.nexware.reservation.entity.ReservationStatus;
import com.nexware.reservation.event.StockReservedEvent;
import com.nexware.reservation.mapper.ReservationMapper;
import com.nexware.reservation.repository.InventoryReservationRepository;
import com.nexware.shared.exception.BusinessRuleViolationException;
import com.nexware.shared.exception.ResourceNotFoundException;
import com.nexware.user.entity.User;
import com.nexware.user.repository.UserRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository reservationRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;
    private final ApplicationProperties properties;
    private final ReservationExpirationCache reservationExpirationCache;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock = Clock.systemUTC();

    @Transactional
    public ReservationResponse reserve(ReservationCreateRequest request, AuthenticatedUser principal) {
        Inventory inventory = inventoryRepository.findForUpdate(request.warehouseId(), request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory record", "%s:%s".formatted(request.warehouseId(), request.productId())));

        if (inventory.availableQuantity() < request.quantity()) {
            throw new BusinessRuleViolationException("Insufficient available inventory to reserve requested quantity");
        }

        User reservedBy = userRepository.findByIdAndDeletedAtIsNull(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", principal.getId()));
        Instant now = Instant.now(clock);
        Duration ttl = request.expirationMinutes() == null
                ? properties.reservation().defaultExpiration()
                : Duration.ofMinutes(request.expirationMinutes());
        String reference = "RSV-" + now.toEpochMilli() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        inventory.reserve(request.quantity());
        InventoryReservation reservation = reservationRepository.save(InventoryReservation.create(
                inventory,
                reservedBy,
                request.quantity(),
                reference,
                now.plus(ttl)
        ));
        reservationExpirationCache.track(reservation.getId(), reference, ttl);
        stockMovementRepository.save(StockMovement.reservation(
                inventory.getProduct(),
                inventory.getWarehouse(),
                request.quantity(),
                "INVENTORY_RESERVATION",
                reservation.getId(),
                StockMovementType.RESERVATION,
                "Stock reserved",
                now
        ));
        eventPublisher.publishEvent(new StockReservedEvent(reservation.getId(), inventory.getProduct().getId(), inventory.getWarehouse().getId(), request.quantity(), now));
        return reservationMapper.toResponse(reservation);
    }

    @Transactional
    public ReservationResponse release(UUID reservationId) {
        InventoryReservation reservation = reservationRepository.findByIdAndStatus(reservationId, ReservationStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active reservation", reservationId));
        return releaseReservation(reservation, ReservationStatus.RELEASED);
    }

    @Transactional
    public int expireDueReservations() {
        Instant now = Instant.now(clock);
        return reservationRepository.findTop100ByStatusAndExpiresAtBefore(ReservationStatus.ACTIVE, now).stream()
                .map(reservation -> releaseReservation(reservation, ReservationStatus.EXPIRED))
                .toList()
                .size();
    }

    @Transactional(readOnly = true)
    public Page<ReservationResponse> list(UUID warehouseId, Pageable pageable) {
        Page<InventoryReservation> page = warehouseId == null
                ? reservationRepository.findAll(pageable)
                : reservationRepository.findAllByWarehouseId(warehouseId, pageable);
        return page.map(reservationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReservationResponse> listByWarehouse(UUID warehouseId, Pageable pageable) {
        return list(warehouseId, pageable);
    }

    @Transactional(readOnly = true)
    public ReservationResponse get(UUID reservationId) {
        return reservationRepository.findById(reservationId)
                .map(reservationMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", reservationId));
    }

    private ReservationResponse releaseReservation(InventoryReservation reservation, ReservationStatus targetStatus) {
        Instant now = Instant.now(clock);
        Inventory inventory = inventoryRepository.findForUpdate(reservation.getWarehouse().getId(), reservation.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory record", reservation.getInventory().getId()));
        inventory.releaseReservation(reservation.getQuantity());
        if (targetStatus == ReservationStatus.EXPIRED) {
            reservation.expire(now);
        } else {
            reservation.release(now);
        }
        reservationExpirationCache.remove(reservation.getId());
        stockMovementRepository.save(StockMovement.reservation(
                reservation.getProduct(),
                reservation.getWarehouse(),
                reservation.getQuantity(),
                "INVENTORY_RESERVATION",
                reservation.getId(),
                StockMovementType.RELEASE,
                targetStatus == ReservationStatus.EXPIRED ? "Reservation expired" : "Reservation released",
                now
        ));
        return reservationMapper.toResponse(reservation);
    }
}
