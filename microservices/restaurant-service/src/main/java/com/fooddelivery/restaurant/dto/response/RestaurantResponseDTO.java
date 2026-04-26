package com.fooddelivery.restaurant.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for restaurant responses (customer view)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponseDTO {

    private Long id;

    private String name;

    private String city;

    @JsonProperty("cuisine_type")
    private String cuisineType;

    private BigDecimal rating;

    @JsonProperty("delivery_time_minutes")
    private Integer deliveryTimeMinutes;

    @JsonProperty("delivery_fee")
    private BigDecimal deliveryFee;

    @JsonProperty("is_open")
    private Boolean isOpen;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("minimum_order_amount")
    private BigDecimal minimumOrderAmount;
}