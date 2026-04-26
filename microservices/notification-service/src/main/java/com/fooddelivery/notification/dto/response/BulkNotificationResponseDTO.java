package com.fooddelivery.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for bulk notification response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkNotificationResponseDTO {

    private int totalRequested;
    private int successfullyCreated;
    private int failed;
    private List<Long> notificationIds;
    private List<String> errors;
}