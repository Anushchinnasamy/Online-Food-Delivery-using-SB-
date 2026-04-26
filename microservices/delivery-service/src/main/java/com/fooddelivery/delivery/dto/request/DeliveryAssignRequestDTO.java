package com.fooddelivery.delivery.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for assigning a delivery to a delivery partner
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAssignRequestDTO {

    @NotNull(message = "Order ID is required")
    @Positive(message = "Order ID must be positive")
    private Long orderId;

    private Long preferredDeliveryPartnerId;

    private Double restaurantLatitude;

    private Double restaurantLongitude;

    private Double customerLatitude;

    private Double customerLongitude;
}