package com.fooddelivery.menu.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for detailed menu item responses (admin view)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemDetailResponseDTO {

    private Long id;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    private String name;

    private String description;

    private BigDecimal price;

    private String category;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("is_available")
    private Boolean isAvailable;

    @JsonProperty("is_vegetarian")
    private Boolean isVegetarian;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}