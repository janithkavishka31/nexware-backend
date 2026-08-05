package com.nexware.reservation.repository;

import com.nexware.reservation.entity.InventoryReservation;
import com.nexware.reservation.entity.ReservationStatus;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, UUID> {

    Page<InventoryReservation> findAllByWarehouseId(UUID warehouseId, Pageable pageable);

    Optional<InventoryReservation> findByReservationReference(String reservationReference);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<InventoryReservation> findByIdAndStatus(UUID id, ReservationStatus status);

    List<InventoryReservation> findTop100ByStatusAndExpiresAtBefore(ReservationStatus status, Instant now);
}
