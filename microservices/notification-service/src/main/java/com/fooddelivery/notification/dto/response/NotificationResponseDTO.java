package com.fooddelivery.notification.dto.response;

import com.fooddelivery.notification.enums.EventType;
import com.fooddelivery.notification.enums.NotificationStatus;
import com.fooddelivery.notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for notification response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {

    private Long id;
    private Long userId;
    private Long referenceId;
    private NotificationType notificationType;
    private EventType eventType;
    private String message;
    private NotificationStatus status;
    private String recipientEmail;
    private String recipientPhone;
    private String subject;
    private Integer retryCount;
    private Integer maxRetryAttempts;
    private LocalDateTime nextRetryAt;
    private LocalDateTime sentAt;
    private LocalDateTime failedAt;
    private String failureReason;
    private String externalId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}