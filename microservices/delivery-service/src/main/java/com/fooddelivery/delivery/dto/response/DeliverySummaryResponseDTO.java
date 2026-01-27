package com.fooddelivery.delivery.dto.response;

import com.fooddelivery.delivery.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for delivery summary response (lightweight version)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliverySummaryResponseDTO {

    private Long id;
    private Long orderId;
    private Long deliveryPartnerId;
    private String deliveryPartnerName;
    private String deliveryPartnerPhone;
    private DeliveryStatus deliveryStatus;
    private LocalDateTime assignedAt;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime deliveredAt;
    private Integer actualDeliveryTimeMinutes;
    private Integer customerRating;
}