package com.fooddelivery.payment.dto.request;

import com.fooddelivery.payment.enums.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for payment status update request (typically from payment gateway)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusUpdateRequestDTO {

    @NotBlank(message = "Transaction ID is required")
    @Size(max = 100, message = "Transaction ID cannot exceed 100 characters")
    private String transactionId;

    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;

    @Size(max = 100, message = "Gateway transaction ID cannot exceed 100 characters")
    private String gatewayTransactionId;

    @Size(max = 50, message = "Gateway name cannot exceed 50 characters")
    private String gatewayName;

    @Size(max = 2000, message = "Gateway response cannot exceed 2000 characters")
    private String gatewayResponse;

    @Size(max = 500, message = "Failure reason cannot exceed 500 characters")
    private String failureReason;
}