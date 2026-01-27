package com.fooddelivery.delivery.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for rejecting a delivery assignment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRejectionRequestDTO {

    @NotBlank(message = "Rejection reason is required")
    @Size(min = 5, max = 500, message = "Rejection reason must be between 5 and 500 characters")
    private String rejectionReason;
}