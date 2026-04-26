package com.fooddelivery.delivery.dto.response;

import com.fooddelivery.delivery.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for delivery partner response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPartnerResponseDTO {

    private Long id;
    private String name;
    private String phone;
    private VehicleType vehicleType;
    private Boolean isActive;
    private Boolean isAvailable;
    private Double currentLatitude;
    private Double currentLongitude;
    private Double rating;
    private Integer totalDeliveries;
    private Integer successfulDeliveries;
    private LocalDateTime lastActiveAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}