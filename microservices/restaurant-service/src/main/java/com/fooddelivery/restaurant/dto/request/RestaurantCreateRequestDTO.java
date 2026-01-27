package com.fooddelivery.restaurant.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * DTO for restaurant creation requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantCreateRequestDTO {

    @NotBlank(message = "Restaurant name is required")
    @Size(min = 2, max = 100, message = "Restaurant name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City name cannot exceed 50 characters")
    private String city;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be 6 digits")
    private String pincode;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private BigDecimal longitude;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Cuisine type is required")
    @Size(max = 50, message = "Cuisine type cannot exceed 50 characters")
    private String cuisineType;

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;

    @NotNull(message = "Opening time is required")
    private LocalTime openingTime;

    @NotNull(message = "Closing time is required")
    private LocalTime closingTime;

    @Min(value = 1, message = "Delivery time must be at least 1 minute")
    @Max(value = 120, message = "Delivery time cannot exceed 120 minutes")
    private Integer deliveryTimeMinutes;

    @DecimalMin(value = "0.0", message = "Minimum order amount cannot be negative")
    private BigDecimal minimumOrderAmount;

    @DecimalMin(value = "0.0", message = "Delivery fee cannot be negative")
    private BigDecimal deliveryFee;
}