package com.fooddelivery.entity;

/**
 * Payment method enumeration
 */
public enum PaymentMethod {
    /**
     * Cash on Delivery
     */
    CASH_ON_DELIVERY("Cash on Delivery"),
    
    /**
     * Credit Card payment
     */
    CREDIT_CARD("Credit Card"),
    
    /**
     * Debit Card payment
     */
    DEBIT_CARD("Debit Card"),
    
    /**
     * UPI payment (Unified Payments Interface)
     */
    UPI("UPI"),
    
    /**
     * Net Banking
     */
    NET_BANKING("Net Banking"),
    
    /**
     * Digital Wallet (PayTM, PhonePe, etc.)
     */
    WALLET("Digital Wallet");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if payment method requires online processing
     */
    public boolean isOnlinePayment() {
        return this != CASH_ON_DELIVERY;
    }

    /**
     * Check if payment method is cash-based
     */
    public boolean isCashPayment() {
        return this == CASH_ON_DELIVERY;
    }

    /**
     * Check if payment method supports instant processing
     */
    public boolean supportsInstantProcessing() {
        return this == UPI || this == WALLET;
    }
}