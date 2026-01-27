package com.fooddelivery.restaurant.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO for restaurant update requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantUpdateRequestDTO {

    @Size(min = 2, max = 100, message = "Restaurant name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 50, message = "Cuisine type cannot exceed 50 characters")
    private String cuisineType;

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;

    private LocalTime openingTime;

    private LocalTime closingTime;

    private Boolean isOpen;
}