package com.fooddelivery.notification.dto.response;

import com.fooddelivery.notification.enums.EventType;
import com.fooddelivery.notification.enums.NotificationStatus;
import com.fooddelivery.notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for notification summary response (lightweight version)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSummaryResponseDTO {

    private Long id;
    private Long userId;
    private Long referenceId;
    private NotificationType notificationType;
    private EventType eventType;
    private String message;
    private NotificationStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}