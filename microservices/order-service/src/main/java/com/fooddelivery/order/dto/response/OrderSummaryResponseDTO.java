package com.fooddelivery.order.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fooddelivery.order.enums.OrderStatus;
import com.fooddelivery.order.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for order summary responses (without items)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryResponseDTO {

    private Long id;

    @JsonProperty("order_number")
    private String orderNumber;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("order_status")
    private OrderStatus orderStatus;

    @JsonProperty("payment_status")
    private PaymentStatus paymentStatus;

    @JsonProperty("item_count")
    private Integer itemCount;

    @JsonProperty("estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}