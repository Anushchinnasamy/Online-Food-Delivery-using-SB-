package com.fooddelivery.order.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for order item responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {

    @JsonProperty("menu_item_id")
    private Long menuItemId;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("item_price")
    private BigDecimal itemPrice;

    private Integer quantity;

    private BigDecimal subtotal;

    @JsonProperty("special_instructions")
    private String specialInstructions;
}