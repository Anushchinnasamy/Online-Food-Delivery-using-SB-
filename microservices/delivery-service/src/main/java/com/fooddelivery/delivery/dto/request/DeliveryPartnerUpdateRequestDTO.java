package com.fooddelivery.delivery.dto.request;

import com.fooddelivery.delivery.enums.VehicleType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating delivery partner information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPartnerUpdateRequestDTO {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(min = 10, max = 15, message = "Phone must be between 10 and 15 characters")
    private String phone;

    private VehicleType vehicleType;

    private Boolean isActive;

    private Boolean isAvailable;

    private Double currentLatitude;

    private Double currentLongitude;
}