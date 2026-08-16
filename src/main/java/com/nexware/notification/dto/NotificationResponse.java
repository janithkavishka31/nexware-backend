package com.nexware.notification.dto;

import com.nexware.notification.entity.NotificationStatus;
import com.nexware.notification.entity.NotificationType;
import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(UUID id, NotificationType type, NotificationStatus status, String title, String message, String payload, Instant createdAt) {
}
