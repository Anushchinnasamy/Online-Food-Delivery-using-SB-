package com.fooddelivery.delivery.dto.request;

import com.fooddelivery.delivery.enums.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating delivery status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStatusUpdateRequestDTO {

    @NotNull(message = "Delivery status is required")
    private DeliveryStatus deliveryStatus;

    @Size(max = 1000, message = "Delivery notes cannot exceed 1000 characters")
    private String deliveryNotes;

    private Double currentLatitude;

    private Double currentLongitude;
}