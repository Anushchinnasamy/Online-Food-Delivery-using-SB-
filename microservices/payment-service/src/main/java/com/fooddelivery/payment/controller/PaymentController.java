package com.fooddelivery.payment.controller;

import com.fooddelivery.payment.dto.request.PaymentInitiateRequestDTO;
import com.fooddelivery.payment.dto.request.PaymentStatusUpdateRequestDTO;
import com.fooddelivery.payment.dto.response.PaymentResponseDTO;
import com.fooddelivery.payment.dto.response.PaymentSummaryResponseDTO;
import com.fooddelivery.payment.enums.PaymentStatus;
import com.fooddelivery.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for payment operations
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Initiate a new payment
     */
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> initiatePayment(
            @Valid @RequestBody PaymentInitiateRequestDTO request,
            @RequestHeader("X-User-Id") Long userId) {
        
        log.info("Initiating payment for order: {} by user: {}", request.getOrderId(), userId);
        PaymentResponseDTO response = paymentService.initiatePayment(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update payment status (webhook endpoint for payment gateways)
     */
    @PutMapping("/status")
    public ResponseEntity<PaymentResponseDTO> updatePaymentStatus(
            @Valid @RequestBody PaymentStatusUpdateRequestDTO request) {
        
        log.info("Updating payment status for transaction: {}", request.getTransactionId());
        PaymentResponseDTO response = paymentService.updatePaymentStatus(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment by transaction ID
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<PaymentResponseDTO> getPayment(@PathVariable String transactionId) {
        log.info("Getting payment details for transaction: {}", transactionId);
        PaymentResponseDTO response = paymentService.getPaymentByTransactionId(transactionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment summary with refunds
     */
    @GetMapping("/{transactionId}/summary")
    public ResponseEntity<PaymentSummaryResponseDTO> getPaymentSummary(@PathVariable String transactionId) {
        log.info("Getting payment summary for transaction: {}", transactionId);
        PaymentSummaryResponseDTO response = paymentService.getPaymentSummary(transactionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payments by order ID
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByOrderId(@PathVariable Long orderId) {
        log.info("Getting payments for order: {}", orderId);
        List<PaymentResponseDTO> response = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user's payments with pagination
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PaymentResponseDTO>> getUserPayments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting payments for user: {} (page: {}, size: {})", userId, page, size);
        Page<PaymentResponseDTO> response = paymentService.getUserPayments(userId, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user's recent payments
     */
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<PaymentResponseDTO>> getUserRecentPayments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Getting recent payments for user: {} (limit: {})", userId, limit);
        List<PaymentResponseDTO> response = paymentService.getUserRecentPayments(userId, limit);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payments by status (admin endpoint)
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        log.info("Getting payments by status: {}", status);
        List<PaymentResponseDTO> response = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel payment
     */
    @PutMapping("/{transactionId}/cancel")
    public ResponseEntity<PaymentResponseDTO> cancelPayment(
            @PathVariable String transactionId,
            @RequestParam String reason) {
        
        log.info("Cancelling payment: {} with reason: {}", transactionId, reason);
        PaymentResponseDTO response = paymentService.cancelPayment(transactionId, reason);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Payment Service is healthy");
    }
}