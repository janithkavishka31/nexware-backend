package com.nexware.notification.mapper;

import com.nexware.notification.dto.NotificationResponse;
import com.nexware.notification.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponse toResponse(Notification notification);
}
