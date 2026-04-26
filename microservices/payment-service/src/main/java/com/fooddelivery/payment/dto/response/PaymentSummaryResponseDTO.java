package com.fooddelivery.payment.dto.response;

import com.fooddelivery.payment.enums.PaymentMethod;
import com.fooddelivery.payment.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for payment summary with refunds
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSummaryResponseDTO {

    private Long id;
    private String transactionId;
    private Long orderId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private BigDecimal refundedAmount;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private List<RefundResponseDTO> refunds;
}