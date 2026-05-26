package com.app.mapper;

import org.springframework.stereotype.Component;

import com.app.dto.response.notification.NotificationResponse;
import com.app.entity.notification.NotificationEntity;

@Component
public class NotificationMapper {
    
    public NotificationResponse toResponse(NotificationEntity notification){

        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .createAt(notification.getCreateAt())
                .build();
    }
}
