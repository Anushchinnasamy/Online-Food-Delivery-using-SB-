package com.fooddelivery.payment.dto.response;

import com.fooddelivery.payment.enums.RefundStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for refund response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponseDTO {

    private Long id;
    private String refundTransactionId;
    private String paymentTransactionId;
    private BigDecimal refundAmount;
    private RefundStatus refundStatus;
    private String refundReason;
    private String gatewayRefundId;
    private String failureReason;
    private LocalDateTime processedAt;
    private String initiatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}