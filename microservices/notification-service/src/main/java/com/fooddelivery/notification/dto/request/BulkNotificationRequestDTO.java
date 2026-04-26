package com.fooddelivery.notification.dto.request;

import com.fooddelivery.notification.enums.EventType;
import com.fooddelivery.notification.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for bulk notification request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkNotificationRequestDTO {

    @NotEmpty(message = "User IDs list cannot be empty")
    private List<Long> userIds;

    private Long referenceId;

    @NotNull(message = "Notification type is required")
    private NotificationType notificationType;

    @NotNull(message = "Event type is required")
    private EventType eventType;

    @NotBlank(message = "Message is required")
    @Size(max = 1000, message = "Message cannot exceed 1000 characters")
    private String message;

    @Size(max = 200, message = "Subject cannot exceed 200 characters")
    private String subject;

    @Size(max = 100, message = "Template name cannot exceed 100 characters")
    private String templateName;

    @Size(max = 2000, message = "Template data cannot exceed 2000 characters")
    private String templateData;

    private Integer maxRetryAttempts = 3;

    @Size(max = 1000, message = "Metadata cannot exceed 1000 characters")
    private String metadata;
}