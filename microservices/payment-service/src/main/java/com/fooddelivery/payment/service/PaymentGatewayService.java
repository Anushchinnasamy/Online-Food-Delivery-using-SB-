package com.fooddelivery.payment.service;

import com.fooddelivery.payment.entity.Payment;
import com.fooddelivery.payment.entity.Refund;
import com.fooddelivery.payment.enums.PaymentStatus;
import com.fooddelivery.payment.enums.RefundStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * Service for payment gateway integration
 * This is a simulation service for demo purposes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentGatewayService {

    private final Random random = new Random();

    /**
     * Process payment through gateway (simulation)
     */
    public void processPayment(Payment payment) {
        log.info("Processing payment through gateway: {}", payment.getTransactionId());

        // Simulate async payment processing
        CompletableFuture.runAsync(() -> {
            try {
                // Simulate processing delay
                Thread.sleep(2000 + random.nextInt(3000)); // 2-5 seconds

                // Simulate payment success/failure (90% success rate)
                boolean isSuccess = random.nextDouble() < 0.9;

                if (isSuccess) {
                    // Simulate successful payment
                    String gatewayTxnId = "GW_" + System.currentTimeMillis() + "_" + random.nextInt(10000);
                    String gatewayResponse = "Payment processed successfully";
                    
                    payment.markAsSuccessful(gatewayTxnId, gatewayResponse);
                    log.info("Payment processed successfully: {}", payment.getTransactionId());
                } else {
                    // Simulate payment failure
                    String failureReason = getRandomFailureReason();
                    String gatewayResponse = "Payment failed: " + failureReason;
                    
                    payment.markAsFailed(failureReason, gatewayResponse);
                    log.warn("Payment failed: {} - {}", payment.getTransactionId(), failureReason);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                payment.markAsFailed("Payment processing interrupted", "System error");
                log.error("Payment processing interrupted: {}", payment.getTransactionId(), e);
            } catch (Exception e) {
                payment.markAsFailed("Payment processing error: " + e.getMessage(), "System error");
                log.error("Payment processing error: {}", payment.getTransactionId(), e);
            }
        });
    }

    /**
     * Process refund through gateway (simulation)
     */
    public void processRefund(Refund refund) {
        log.info("Processing refund through gateway: {}", refund.getRefundTransactionId());

        // Simulate async refund processing
        CompletableFuture.runAsync(() -> {
            try {
                // Simulate processing delay
                Thread.sleep(1000 + random.nextInt(2000)); // 1-3 seconds

                // Simulate refund success/failure (95% success rate)
                boolean isSuccess = random.nextDouble() < 0.95;

                if (isSuccess) {
                    // Simulate successful refund
                    String gatewayRefundId = "GW_REF_" + System.currentTimeMillis() + "_" + random.nextInt(10000);
                    String gatewayResponse = "Refund processed successfully";
                    
                    refund.markAsSuccessful(gatewayRefundId, gatewayResponse);
                    log.info("Refund processed successfully: {}", refund.getRefundTransactionId());
                } else {
                    // Simulate refund failure
                    String failureReason = getRandomRefundFailureReason();
                    String gatewayResponse = "Refund failed: " + failureReason;
                    
                    refund.markAsFailed(failureReason, gatewayResponse);
                    log.warn("Refund failed: {} - {}", refund.getRefundTransactionId(), failureReason);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                refund.markAsFailed("Refund processing interrupted", "System error");
                log.error("Refund processing interrupted: {}", refund.getRefundTransactionId(), e);
            } catch (Exception e) {
                refund.markAsFailed("Refund processing error: " + e.getMessage(), "System error");
                log.error("Refund processing error: {}", refund.getRefundTransactionId(), e);
            }
        });
    }

    /**
     * Verify payment status with gateway (simulation)
     */
    public PaymentStatus verifyPaymentStatus(String gatewayTransactionId) {
        log.info("Verifying payment status with gateway: {}", gatewayTransactionId);
        
        // Simulate gateway verification
        try {
            Thread.sleep(500 + random.nextInt(1000)); // 0.5-1.5 seconds
            
            // Simulate different statuses
            double rand = random.nextDouble();
            if (rand < 0.8) {
                return PaymentStatus.SUCCESS;
            } else if (rand < 0.9) {
                return PaymentStatus.PROCESSING;
            } else {
                return PaymentStatus.FAILED;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Payment verification interrupted: {}", gatewayTransactionId, e);
            return PaymentStatus.FAILED;
        }
    }

    /**
     * Verify refund status with gateway (simulation)
     */
    public RefundStatus verifyRefundStatus(String gatewayRefundId) {
        log.info("Verifying refund status with gateway: {}", gatewayRefundId);
        
        // Simulate gateway verification
        try {
            Thread.sleep(500 + random.nextInt(1000)); // 0.5-1.5 seconds
            
            // Simulate different statuses
            double rand = random.nextDouble();
            if (rand < 0.85) {
                return RefundStatus.SUCCESS;
            } else if (rand < 0.95) {
                return RefundStatus.PROCESSING;
            } else {
                return RefundStatus.FAILED;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Refund verification interrupted: {}", gatewayRefundId, e);
            return RefundStatus.FAILED;
        }
    }

    // Private helper methods

    private String getRandomFailureReason() {
        String[] reasons = {
            "Insufficient funds",
            "Card declined",
            "Invalid card details",
            "Transaction limit exceeded",
            "Bank server error",
            "Network timeout",
            "Invalid OTP",
            "Card expired"
        };
        return reasons[random.nextInt(reasons.length)];
    }

    private String getRandomRefundFailureReason() {
        String[] reasons = {
            "Original transaction not found",
            "Refund limit exceeded",
            "Bank processing error",
            "Account closed",
            "Invalid refund amount",
            "Gateway timeout"
        };
        return reasons[random.nextInt(reasons.length)];
    }
}