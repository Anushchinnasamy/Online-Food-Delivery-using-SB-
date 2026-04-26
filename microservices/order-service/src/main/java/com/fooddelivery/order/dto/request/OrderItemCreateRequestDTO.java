package com.fooddelivery.order.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for order item creation within an order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemCreateRequestDTO {

    @NotNull(message = "Menu item ID is required")
    private Long menuItemId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 99, message = "Quantity cannot exceed 99")
    private Integer quantity;

    @Size(max = 200, message = "Special instructions cannot exceed 200 characters")
    private String specialInstructions;
}