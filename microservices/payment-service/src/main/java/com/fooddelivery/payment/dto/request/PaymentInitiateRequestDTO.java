package com.fooddelivery.payment.dto.request;

import com.fooddelivery.payment.enums.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for payment initiation request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitiateRequestDTO {

    @NotNull(message = "Order ID is required")
    @Positive(message = "Order ID must be positive")
    private Long orderId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 integer digits and 2 decimal places")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Size(max = 1000, message = "Metadata cannot exceed 1000 characters")
    private String metadata;

    @Size(max = 3, message = "Currency code must be 3 characters")
    private String currency = "INR";
}