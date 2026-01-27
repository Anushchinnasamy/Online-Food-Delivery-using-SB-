package com.fooddelivery.payment.service;

import com.fooddelivery.payment.dto.request.PaymentInitiateRequestDTO;
import com.fooddelivery.payment.dto.request.PaymentStatusUpdateRequestDTO;
import com.fooddelivery.payment.dto.response.PaymentResponseDTO;
import com.fooddelivery.payment.dto.response.PaymentSummaryResponseDTO;
import com.fooddelivery.payment.entity.Payment;
import com.fooddelivery.payment.enums.PaymentStatus;
import com.fooddelivery.payment.exception.PaymentNotFoundException;
import com.fooddelivery.payment.exception.PaymentProcessingException;
import com.fooddelivery.payment.mapper.PaymentMapper;
import com.fooddelivery.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service class for payment operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentGatewayService paymentGatewayService;
    private final OrderServiceClient orderServiceClient;

    /**
     * Initiate a new payment
     */
    public PaymentResponseDTO initiatePayment(PaymentInitiateRequestDTO request, Long userId) {
        log.info("Initiating payment for order: {} by user: {}", request.getOrderId(), userId);

        // Validate order exists and belongs to user
        validateOrderForPayment(request.getOrderId(), userId);

        // Check if payment already exists for this order
        if (paymentRepository.existsByOrderIdAndPaymentStatus(request.getOrderId(), PaymentStatus.SUCCESS)) {
            throw new PaymentProcessingException("Payment already completed for order: " + request.getOrderId());
        }

        // Create payment entity
        Payment payment = paymentMapper.toEntity(request);
        payment.setUserId(userId);
        payment.setTransactionId(generateTransactionId());
        payment.setPaymentStatus(PaymentStatus.INITIATED);

        // Save payment
        payment = paymentRepository.save(payment);

        // Process payment based on method
        try {
            if (request.getPaymentMethod().requiresOnlineProcessing()) {
                // Process online payment
                paymentGatewayService.processPayment(payment);
            } else {
                // For COD, mark as successful immediately
                payment.markAsSuccessful("COD_" + payment.getTransactionId(), "Cash on Delivery");
                payment = paymentRepository.save(payment);
                
                // Update order payment status
                updateOrderPaymentStatus(payment.getOrderId(), PaymentStatus.SUCCESS);
            }
        } catch (Exception e) {
            log.error("Payment processing failed for transaction: {}", payment.getTransactionId(), e);
            payment.markAsFailed("Payment processing failed: " + e.getMessage(), null);
            paymentRepository.save(payment);
            throw new PaymentProcessingException("Payment processing failed", e);
        }

        log.info("Payment initiated successfully with transaction ID: {}", payment.getTransactionId());
        return paymentMapper.toResponseDTO(payment);
    }

    /**
     * Update payment status (typically called by payment gateway webhook)
     */
    public PaymentResponseDTO updatePaymentStatus(PaymentStatusUpdateRequestDTO request) {
        log.info("Updating payment status for transaction: {}", request.getTransactionId());

        Payment payment = paymentRepository.findByTransactionId(request.getTransactionId())
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + request.getTransactionId()));

        try {
            // Update payment status
            payment.updateStatus(request.getPaymentStatus());
            payment.setGatewayTransactionId(request.getGatewayTransactionId());
            payment.setGatewayName(request.getGatewayName());
            payment.setGatewayResponse(request.getGatewayResponse());
            
            if (request.getPaymentStatus() == PaymentStatus.FAILED) {
                payment.setFailureReason(request.getFailureReason());
            }

            payment = paymentRepository.save(payment);

            // Update order payment status
            updateOrderPaymentStatus(payment.getOrderId(), request.getPaymentStatus());

            log.info("Payment status updated successfully for transaction: {}", request.getTransactionId());
            return paymentMapper.toResponseDTO(payment);

        } catch (Exception e) {
            log.error("Failed to update payment status for transaction: {}", request.getTransactionId(), e);
            throw new PaymentProcessingException("Failed to update payment status", e);
        }
    }

    /**
     * Get payment by transaction ID
     */
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + transactionId));
        
        return paymentMapper.toResponseDTO(payment);
    }

    /**
     * Get payment summary with refunds
     */
    @Transactional(readOnly = true)
    public PaymentSummaryResponseDTO getPaymentSummary(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + transactionId));
        
        return paymentMapper.toSummaryResponseDTO(payment);
    }

    /**
     * Get payments by order ID
     */
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByOrderId(Long orderId) {
        List<Payment> payments = paymentRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
        return paymentMapper.toResponseDTOList(payments);
    }

    /**
     * Get user's payments with pagination
     */
    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> getUserPayments(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Payment> payments = paymentRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return payments.map(paymentMapper::toResponseDTO);
    }

    /**
     * Get user's recent payments
     */
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getUserRecentPayments(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Payment> payments = paymentRepository.findRecentPaymentsByUserId(userId, pageable);
        return paymentMapper.toResponseDTOList(payments);
    }

    /**
     * Get payments by status
     */
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus status) {
        List<Payment> payments = paymentRepository.findByPaymentStatus(status);
        return paymentMapper.toResponseDTOList(payments);
    }

    /**
     * Cancel payment (if possible)
     */
    public PaymentResponseDTO cancelPayment(String transactionId, String reason) {
        log.info("Cancelling payment: {}", transactionId);

        Payment payment = paymentRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + transactionId));

        if (payment.getPaymentStatus().isFinalState()) {
            throw new PaymentProcessingException("Cannot cancel payment in final state: " + payment.getPaymentStatus());
        }

        try {
            payment.updateStatus(PaymentStatus.CANCELLED);
            payment.setFailureReason(reason);
            payment = paymentRepository.save(payment);

            // Update order payment status
            updateOrderPaymentStatus(payment.getOrderId(), PaymentStatus.CANCELLED);

            log.info("Payment cancelled successfully: {}", transactionId);
            return paymentMapper.toResponseDTO(payment);

        } catch (Exception e) {
            log.error("Failed to cancel payment: {}", transactionId, e);
            throw new PaymentProcessingException("Failed to cancel payment", e);
        }
    }

    /**
     * Process pending payments cleanup
     */
    @Transactional
    public void processPendingPaymentsCleanup() {
        log.info("Processing pending payments cleanup");
        
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(30); // 30 minutes timeout
        List<Payment> pendingPayments = paymentRepository.findPendingPaymentsOlderThan(cutoffTime);
        
        for (Payment payment : pendingPayments) {
            try {
                payment.markAsFailed("Payment timeout", "System timeout after 30 minutes");
                paymentRepository.save(payment);
                
                // Update order payment status
                updateOrderPaymentStatus(payment.getOrderId(), PaymentStatus.FAILED);
                
                log.info("Marked payment as failed due to timeout: {}", payment.getTransactionId());
            } catch (Exception e) {
                log.error("Failed to process timeout for payment: {}", payment.getTransactionId(), e);
            }
        }
        
        log.info("Processed {} pending payments for cleanup", pendingPayments.size());
    }

    // Private helper methods

    private String generateTransactionId() {
        return "PAY_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void validateOrderForPayment(Long orderId, Long userId) {
        try {
            // Call order service to validate order
            orderServiceClient.validateOrderForPayment(orderId, userId);
        } catch (Exception e) {
            log.error("Order validation failed for order: {} and user: {}", orderId, userId, e);
            throw new PaymentProcessingException("Order validation failed", e);
        }
    }

    private void updateOrderPaymentStatus(Long orderId, PaymentStatus paymentStatus) {
        try {
            // Call order service to update payment status
            orderServiceClient.updateOrderPaymentStatus(orderId, paymentStatus);
        } catch (Exception e) {
            log.error("Failed to update order payment status for order: {}", orderId, e);
            // Don't throw exception here as payment is already processed
        }
    }
}