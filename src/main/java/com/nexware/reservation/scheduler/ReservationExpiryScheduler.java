package com.nexware.reservation.scheduler;

import com.nexware.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "nexware.scheduler.reservation-expiry-enabled", havingValue = "true", matchIfMissing = true)
public class ReservationExpiryScheduler {

    private final ReservationService reservationService;

    @Scheduled(cron = "${nexware.scheduler.reservation-expiry-cron:0 */5 * * * *}")
    public void expireDueReservations() {
        int expired = reservationService.expireDueReservations();
        if (expired > 0) {
            log.info("Automatically expired {} inventory reservations", expired);
        }
    }
}
