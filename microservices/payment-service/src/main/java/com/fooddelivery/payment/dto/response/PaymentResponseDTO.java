package com.fooddelivery.payment.dto.response;

import com.fooddelivery.payment.enums.PaymentMethod;
import com.fooddelivery.payment.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for payment response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

    private Long id;
    private String transactionId;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String gatewayTransactionId;
    private String gatewayName;
    private String failureReason;
    private LocalDateTime processedAt;
    private BigDecimal refundedAmount;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}