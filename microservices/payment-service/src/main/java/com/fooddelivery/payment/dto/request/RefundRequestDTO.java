package com.fooddelivery.payment.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for refund request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestDTO {

    @NotBlank(message = "Payment transaction ID is required")
    @Size(max = 100, message = "Payment transaction ID cannot exceed 100 characters")
    private String paymentTransactionId;

    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "0.01", message = "Refund amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Refund amount must have at most 10 integer digits and 2 decimal places")
    private BigDecimal refundAmount;

    @NotBlank(message = "Refund reason is required")
    @Size(min = 5, max = 500, message = "Refund reason must be between 5 and 500 characters")
    private String refundReason;

    @Size(max = 50, message = "Initiated by cannot exceed 50 characters")
    private String initiatedBy = "SYSTEM";

    @Size(max = 1000, message = "Metadata cannot exceed 1000 characters")
    private String metadata;
}