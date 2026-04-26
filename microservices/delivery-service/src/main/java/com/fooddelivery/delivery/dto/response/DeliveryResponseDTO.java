package com.fooddelivery.delivery.dto.response;

import com.fooddelivery.delivery.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for delivery response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponseDTO {

    private Long id;
    private Long orderId;
    private Long deliveryPartnerId;
    private DeliveryStatus deliveryStatus;
    private LocalDateTime assignedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime onTheWayAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime estimatedDeliveryTime;
    private Integer actualDeliveryTimeMinutes;
    private Integer rejectionCount;
    private String rejectionReason;
    private String cancellationReason;
    private String deliveryNotes;
    private Integer customerRating;
    private String customerFeedback;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}