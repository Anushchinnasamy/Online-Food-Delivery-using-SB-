package com.fooddelivery.order.service;

import com.fooddelivery.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Service for generating unique order numbers
 */
@Service
@RequiredArgsConstructor
public class OrderNumberGeneratorService {

    private final OrderRepository orderRepository;
    private final Random random = new Random();

    /**
     * Generate a unique order number
     * Format: ORD-YYYYMMDD-HHMMSS-XXXX
     */
    public String generateOrderNumber() {
        String orderNumber;
        int attempts = 0;
        int maxAttempts = 10;

        do {
            orderNumber = createOrderNumber();
            attempts++;
        } while (orderRepository.existsByOrderNumber(orderNumber) && attempts < maxAttempts);

        if (attempts >= maxAttempts) {
            throw new RuntimeException("Failed to generate unique order number after " + maxAttempts + " attempts");
        }

        return orderNumber;
    }

    private String createOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timePart = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        String randomPart = String.format("%04d", random.nextInt(10000));
        
        return String.format("ORD-%s-%s-%s", datePart, timePart, randomPart);
    }
}