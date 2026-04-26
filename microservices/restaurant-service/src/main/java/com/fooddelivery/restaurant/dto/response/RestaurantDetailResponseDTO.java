package com.fooddelivery.restaurant.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO for detailed restaurant responses (admin view)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDetailResponseDTO {

    private Long id;

    private String name;

    private String description;

    private String address;

    private String city;

    private String pincode;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String phone;

    private String email;

    @JsonProperty("cuisine_type")
    private String cuisineType;

    @JsonProperty("image_url")
    private String imageUrl;

    private BigDecimal rating;

    @JsonProperty("total_reviews")
    private Integer totalReviews;

    @JsonProperty("delivery_time_minutes")
    private Integer deliveryTimeMinutes;

    @JsonProperty("minimum_order_amount")
    private BigDecimal minimumOrderAmount;

    @JsonProperty("delivery_fee")
    private BigDecimal deliveryFee;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("is_approved")
    private Boolean isApproved;

    @JsonProperty("is_open")
    private Boolean isOpen;

    @JsonProperty("opening_time")
    private LocalTime openingTime;

    @JsonProperty("closing_time")
    private LocalTime closingTime;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}