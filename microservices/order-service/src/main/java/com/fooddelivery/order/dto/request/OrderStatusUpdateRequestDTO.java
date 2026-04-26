package com.fooddelivery.order.dto.request;

import com.fooddelivery.order.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for order status update requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequestDTO {

    @NotNull(message = "Order status is required")
    private OrderStatus orderStatus;

    private String notes;
}