package com.fooddelivery.payment.service;

import com.fooddelivery.payment.dto.request.RefundRequestDTO;
import com.fooddelivery.payment.dto.response.RefundResponseDTO;
import com.fooddelivery.payment.entity.Payment;
import com.fooddelivery.payment.entity.Refund;
import com.fooddelivery.payment.enums.RefundStatus;
import com.fooddelivery.payment.exception.PaymentNotFoundException;
import com.fooddelivery.payment.exception.RefundNotFoundException;
import com.fooddelivery.payment.exception.RefundProcessingException;
import com.fooddelivery.payment.mapper.RefundMapper;
import com.fooddelivery.payment.repository.PaymentRepository;
import com.fooddelivery.payment.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service class for refund operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RefundService {

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final RefundMapper refundMapper;
    private final PaymentGatewayService paymentGatewayService;

    /**
     * Initiate a refund
     */
    public RefundResponseDTO initiateRefund(RefundRequestDTO request) {
        log.info("Initiating refund for payment: {}", request.getPaymentTransactionId());

        // Find the payment
        Payment payment = paymentRepository.findByTransactionId(request.getPaymentTransactionId())
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + request.getPaymentTransactionId()));

        // Validate refund eligibility
        validateRefundEligibility(payment, request.getRefundAmount());

        // Create refund entity
        Refund refund = refundMapper.toEntity(request);
        refund.setPayment(payment);
        refund.setRefundTransactionId(generateRefundTransactionId());
        refund.setRefundStatus(RefundStatus.INITIATED);

        // Save refund
        refund = refundRepository.save(refund);

        // Process refund based on payment method
        try {
            if (payment.getPaymentMethod().supportsRefund()) {
                // Process online refund
                paymentGatewayService.processRefund(refund);
            } else {
                // For COD or non-refundable methods, mark as successful immediately
                refund.markAsSuccessful("COD_REFUND_" + refund.getRefundTransactionId(), "Cash on Delivery Refund");
                refund = refundRepository.save(refund);
            }
        } catch (Exception e) {
            log.error("Refund processing failed for transaction: {}", refund.getRefundTransactionId(), e);
            refund.markAsFailed("Refund processing failed: " + e.getMessage(), null);
            refundRepository.save(refund);
            throw new RefundProcessingException("Refund processing failed", e);
        }

        log.info("Refund initiated successfully with transaction ID: {}", refund.getRefundTransactionId());
        return refundMapper.toResponseDTO(refund);
    }

    /**
     * Update refund status (typically called by payment gateway webhook)
     */
    public RefundResponseDTO updateRefundStatus(String refundTransactionId, RefundStatus status, 
                                              String gatewayRefundId, String gatewayResponse, String failureReason) {
        log.info("Updating refund status for transaction: {}", refundTransactionId);

        Refund refund = refundRepository.findByRefundTransactionId(refundTransactionId)
            .orElseThrow(() -> new RefundNotFoundException("Refund not found: " + refundTransactionId));

        try {
            if (status == RefundStatus.SUCCESS) {
                refund.markAsSuccessful(gatewayRefundId, gatewayResponse);
            } else if (status == RefundStatus.FAILED) {
                refund.markAsFailed(failureReason, gatewayResponse);
            } else {
                refund.updateStatus(status);
                refund.setGatewayRefundId(gatewayRefundId);
                refund.setGatewayResponse(gatewayResponse);
            }

            refund = refundRepository.save(refund);

            log.info("Refund status updated successfully for transaction: {}", refundTransactionId);
            return refundMapper.toResponseDTO(refund);

        } catch (Exception e) {
            log.error("Failed to update refund status for transaction: {}", refundTransactionId, e);
            throw new RefundProcessingException("Failed to update refund status", e);
        }
    }

    /**
     * Get refund by transaction ID
     */
    @Transactional(readOnly = true)
    public RefundResponseDTO getRefundByTransactionId(String refundTransactionId) {
        Refund refund = refundRepository.findByRefundTransactionId(refundTransactionId)
            .orElseThrow(() -> new RefundNotFoundException("Refund not found: " + refundTransactionId));
        
        return refundMapper.toResponseDTO(refund);
    }

    /**
     * Get refunds by payment transaction ID
     */
    @Transactional(readOnly = true)
    public List<RefundResponseDTO> getRefundsByPaymentTransactionId(String paymentTransactionId) {
        List<Refund> refunds = refundRepository.findByPaymentTransactionId(paymentTransactionId);
        return refundMapper.toResponseDTOList(refunds);
    }

    /**
     * Get user's refunds with pagination
     */
    @Transactional(readOnly = true)
    public Page<RefundResponseDTO> getUserRefunds(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Refund> refunds = refundRepository.findByUserId(userId, pageable);
        return refunds.map(refundMapper::toResponseDTO);
    }

    /**
     * Get refunds by status
     */
    @Transactional(readOnly = true)
    public List<RefundResponseDTO> getRefundsByStatus(RefundStatus status) {
        List<Refund> refunds = refundRepository.findByRefundStatus(status);
        return refundMapper.toResponseDTOList(refunds);
    }

    /**
     * Cancel refund (if possible)
     */
    public RefundResponseDTO cancelRefund(String refundTransactionId, String reason) {
        log.info("Cancelling refund: {}", refundTransactionId);

        Refund refund = refundRepository.findByRefundTransactionId(refundTransactionId)
            .orElseThrow(() -> new RefundNotFoundException("Refund not found: " + refundTransactionId));

        if (refund.getRefundStatus().isFinalState()) {
            throw new RefundProcessingException("Cannot cancel refund in final state: " + refund.getRefundStatus());
        }

        try {
            refund.updateStatus(RefundStatus.CANCELLED);
            refund.setFailureReason(reason);
            refund = refundRepository.save(refund);

            log.info("Refund cancelled successfully: {}", refundTransactionId);
            return refundMapper.toResponseDTO(refund);

        } catch (Exception e) {
            log.error("Failed to cancel refund: {}", refundTransactionId, e);
            throw new RefundProcessingException("Failed to cancel refund", e);
        }
    }

    /**
     * Process pending refunds cleanup
     */
    @Transactional
    public void processPendingRefundsCleanup() {
        log.info("Processing pending refunds cleanup");
        
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24); // 24 hours timeout
        List<Refund> pendingRefunds = refundRepository.findPendingRefundsOlderThan(cutoffTime);
        
        for (Refund refund : pendingRefunds) {
            try {
                refund.markAsFailed("Refund timeout", "System timeout after 24 hours");
                refundRepository.save(refund);
                
                log.info("Marked refund as failed due to timeout: {}", refund.getRefundTransactionId());
            } catch (Exception e) {
                log.error("Failed to process timeout for refund: {}", refund.getRefundTransactionId(), e);
            }
        }
        
        log.info("Processed {} pending refunds for cleanup", pendingRefunds.size());
    }

    // Private helper methods

    private String generateRefundTransactionId() {
        return "REF_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void validateRefundEligibility(Payment payment, BigDecimal refundAmount) {
        // Check if payment can be refunded
        if (!payment.canBeRefunded()) {
            throw new RefundProcessingException("Payment cannot be refunded. Status: " + payment.getPaymentStatus());
        }

        // Check if payment method supports refund
        if (!payment.getPaymentMethod().supportsRefund()) {
            throw new RefundProcessingException("Payment method does not support refund: " + payment.getPaymentMethod());
        }

        // Check refund amount
        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RefundProcessingException("Refund amount must be greater than zero");
        }

        if (refundAmount.compareTo(payment.getRefundableAmount()) > 0) {
            throw new RefundProcessingException(
                String.format("Refund amount %.2f exceeds refundable amount %.2f", 
                    refundAmount, payment.getRefundableAmount())
            );
        }
    }
}