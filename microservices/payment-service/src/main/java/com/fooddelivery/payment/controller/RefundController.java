package com.fooddelivery.payment.controller;

import com.fooddelivery.payment.dto.request.RefundRequestDTO;
import com.fooddelivery.payment.dto.response.RefundResponseDTO;
import com.fooddelivery.payment.enums.RefundStatus;
import com.fooddelivery.payment.service.RefundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for refund operations
 */
@RestController
@RequestMapping("/refunds")
@RequiredArgsConstructor
@Slf4j
public class RefundController {

    private final RefundService refundService;

    /**
     * Initiate a new refund
     */
    @PostMapping
    public ResponseEntity<RefundResponseDTO> initiateRefund(@Valid @RequestBody RefundRequestDTO request) {
        log.info("Initiating refund for payment: {}", request.getPaymentTransactionId());
        RefundResponseDTO response = refundService.initiateRefund(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update refund status (webhook endpoint for payment gateways)
     */
    @PutMapping("/{refundTransactionId}/status")
    public ResponseEntity<RefundResponseDTO> updateRefundStatus(
            @PathVariable String refundTransactionId,
            @RequestParam RefundStatus status,
            @RequestParam(required = false) String gatewayRefundId,
            @RequestParam(required = false) String gatewayResponse,
            @RequestParam(required = false) String failureReason) {
        
        log.info("Updating refund status for transaction: {} to {}", refundTransactionId, status);
        RefundResponseDTO response = refundService.updateRefundStatus(
            refundTransactionId, status, gatewayRefundId, gatewayResponse, failureReason);
        return ResponseEntity.ok(response);
    }

    /**
     * Get refund by transaction ID
     */
    @GetMapping("/{refundTransactionId}")
    public ResponseEntity<RefundResponseDTO> getRefund(@PathVariable String refundTransactionId) {
        log.info("Getting refund details for transaction: {}", refundTransactionId);
        RefundResponseDTO response = refundService.getRefundByTransactionId(refundTransactionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get refunds by payment transaction ID
     */
    @GetMapping("/payment/{paymentTransactionId}")
    public ResponseEntity<List<RefundResponseDTO>> getRefundsByPaymentTransactionId(
            @PathVariable String paymentTransactionId) {
        
        log.info("Getting refunds for payment: {}", paymentTransactionId);
        List<RefundResponseDTO> response = refundService.getRefundsByPaymentTransactionId(paymentTransactionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user's refunds with pagination
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<RefundResponseDTO>> getUserRefunds(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting refunds for user: {} (page: {}, size: {})", userId, page, size);
        Page<RefundResponseDTO> response = refundService.getUserRefunds(userId, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Get refunds by status (admin endpoint)
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RefundResponseDTO>> getRefundsByStatus(@PathVariable RefundStatus status) {
        log.info("Getting refunds by status: {}", status);
        List<RefundResponseDTO> response = refundService.getRefundsByStatus(status);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel refund
     */
    @PutMapping("/{refundTransactionId}/cancel")
    public ResponseEntity<RefundResponseDTO> cancelRefund(
            @PathVariable String refundTransactionId,
            @RequestParam String reason) {
        
        log.info("Cancelling refund: {} with reason: {}", refundTransactionId, reason);
        RefundResponseDTO response = refundService.cancelRefund(refundTransactionId, reason);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Refund Service is healthy");
    }
}