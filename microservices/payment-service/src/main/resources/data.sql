-- Sample data for Payment Service
-- This file will be executed on application startup if spring.jpa.hibernate.ddl-auto is set to create or create-drop

-- Insert sample payments
INSERT INTO payments (
    transaction_id, order_id, user_id, amount, payment_method, payment_status,
    gateway_transaction_id, gateway_name, gateway_response, currency,
    created_at, updated_at, processed_at
) VALUES 
(
    'PAY_1704067200000_SAMPLE01', 1, 1, 299.50, 'UPI', 'SUCCESS',
    'GW_1704067200000_1001', 'RazorPay', 'Payment processed successfully', 'INR',
    '2024-01-01 10:00:00', '2024-01-01 10:02:00', '2024-01-01 10:02:00'
),
(
    'PAY_1704153600000_SAMPLE02', 2, 2, 450.75, 'CARD', 'SUCCESS',
    'GW_1704153600000_1002', 'Stripe', 'Payment processed successfully', 'INR',
    '2024-01-02 11:00:00', '2024-01-02 11:03:00', '2024-01-02 11:03:00'
),
(
    'PAY_1704240000000_SAMPLE03', 3, 1, 199.00, 'COD', 'SUCCESS',
    'COD_PAY_1704240000000_SAMPLE03', 'COD', 'Cash on Delivery', 'INR',
    '2024-01-03 12:00:00', '2024-01-03 12:00:00', '2024-01-03 12:00:00'
),
(
    'PAY_1704326400000_SAMPLE04', 4, 3, 350.25, 'WALLET', 'FAILED',
    NULL, 'PayTM', 'Insufficient wallet balance', 'INR',
    '2024-01-04 13:00:00', '2024-01-04 13:01:00', '2024-01-04 13:01:00'
),
(
    'PAY_1704412800000_SAMPLE05', 5, 2, 525.00, 'UPI', 'PROCESSING',
    'GW_1704412800000_1005', 'PhonePe', 'Payment in progress', 'INR',
    '2024-01-05 14:00:00', '2024-01-05 14:00:00', NULL
);

-- Insert sample refunds
INSERT INTO refunds (
    refund_transaction_id, payment_id, refund_amount, refund_status, refund_reason,
    gateway_refund_id, gateway_response, initiated_by,
    created_at, updated_at, processed_at
) VALUES 
(
    'REF_1704499200000_SAMPLE01', 1, 299.50, 'SUCCESS', 'Order cancelled by customer',
    'GW_REF_1704499200000_2001', 'Refund processed successfully', 'USER',
    '2024-01-06 15:00:00', '2024-01-06 15:05:00', '2024-01-06 15:05:00'
),
(
    'REF_1704585600000_SAMPLE02', 2, 225.38, 'SUCCESS', 'Partial refund for missing items',
    'GW_REF_1704585600000_2002', 'Partial refund processed', 'RESTAURANT',
    '2024-01-07 16:00:00', '2024-01-07 16:03:00', '2024-01-07 16:03:00'
),
(
    'REF_1704672000000_SAMPLE03', 2, 225.37, 'PROCESSING', 'Remaining amount refund',
    'GW_REF_1704672000000_2003', 'Refund in progress', 'ADMIN',
    '2024-01-08 17:00:00', '2024-01-08 17:00:00', NULL
);

-- Update refunded amounts in payments table
UPDATE payments SET refunded_amount = 299.50, payment_status = 'REFUNDED' WHERE id = 1;
UPDATE payments SET refunded_amount = 225.38, payment_status = 'PARTIALLY_REFUNDED' WHERE id = 2;