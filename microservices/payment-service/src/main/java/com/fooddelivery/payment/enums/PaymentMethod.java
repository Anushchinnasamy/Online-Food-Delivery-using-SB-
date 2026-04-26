package com.fooddelivery.payment.enums;

/**
 * Payment method enumeration
 */
public enum PaymentMethod {
    UPI("UPI Payment"),
    CARD("Credit/Debit Card"),
    WALLET("Digital Wallet"),
    COD("Cash on Delivery");

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
    public boolean requiresOnlineProcessing() {
        return this != COD;
    }

    /**
     * Check if payment method supports refunds
     */
    public boolean supportsRefund() {
        return this != COD;
    }
}