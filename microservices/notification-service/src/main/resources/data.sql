-- Sample data for Notification Service
-- This file will be executed on application startup if spring.jpa.hibernate.ddl-auto is set to create or create-drop

-- Insert sample notifications
INSERT INTO notifications (
    user_id, reference_id, notification_type, event_type, message, status,
    recipient_email, recipient_phone, subject, retry_count, max_retry_attempts,
    sent_at, external_id, created_at, updated_at
) VALUES 
(
    1, 1001, 'EMAIL', 'USER_REGISTERED', 'Welcome to FoodDelivery! Your account has been created successfully.',
    'SENT', 'user1@example.com', '+919876543210', 'Welcome to FoodDelivery',
    0, 3, '2024-01-15 10:05:00', 'EMAIL_1705312500000_1234',
    '2024-01-15 10:00:00', '2024-01-15 10:05:00'
),
(
    1, 2001, 'SMS', 'ORDER_PLACED', 'Your order #ORD001 has been placed successfully. Total: ₹299.50',
    'SENT', 'user1@example.com', '+919876543210', NULL,
    0, 3, '2024-01-15 11:02:00', 'SMS_1705316520000_5678',
    '2024-01-15 11:00:00', '2024-01-15 11:02:00'
),
(
    1, 2001, 'PUSH', 'ORDER_ACCEPTED', 'Great news! Your order has been accepted by the restaurant.',
    'SENT', NULL, NULL, NULL,
    0, 3, '2024-01-15 11:15:00', 'PUSH_1705317300000_9012',
    '2024-01-15 11:10:00', '2024-01-15 11:15:00'
),
(
    2, 3001, 'EMAIL', 'PAYMENT_SUCCESS', 'Your payment of ₹450.75 has been processed successfully.',
    'SENT', 'user2@example.com', '+919876543211', 'Payment Confirmation',
    0, 3, '2024-01-15 12:05:00', 'EMAIL_1705320300000_3456',
    '2024-01-15 12:00:00', '2024-01-15 12:05:00'
),
(
    2, 4001, 'SMS', 'DELIVERY_ASSIGNED', 'Your delivery partner Rajesh (★4.5) has been assigned. Track your order!',
    'SENT', 'user2@example.com', '+919876543211', NULL,
    0, 3, '2024-01-15 12:32:00', 'SMS_1705321920000_7890',
    '2024-01-15 12:30:00', '2024-01-15 12:32:00'
),
(
    3, 5001, 'PUSH', 'ORDER_DELIVERED', 'Your order has been delivered! Enjoy your meal and rate your experience.',
    'SENT', NULL, NULL, NULL,
    0, 3, '2024-01-15 13:45:00', 'PUSH_1705326300000_2345',
    '2024-01-15 13:40:00', '2024-01-15 13:45:00'
),
(
    1, 6001, 'EMAIL', 'PAYMENT_FAILED', 'Your payment could not be processed. Please try again or use a different payment method.',
    'SENT', 'user1@example.com', '+919876543210', 'Payment Failed',
    0, 3, '2024-01-15 14:10:00', 'EMAIL_1705327800000_6789',
    '2024-01-15 14:05:00', '2024-01-15 14:10:00'
),
(
    4, 7001, 'SMS', 'ORDER_CANCELLED', 'Your order #ORD007 has been cancelled. Refund will be processed within 3-5 business days.',
    'FAILED', 'user4@example.com', '+919876543213', NULL,
    2, 3, NULL, NULL,
    '2024-01-15 15:00:00', '2024-01-15 15:10:00'
),
(
    5, NULL, 'EMAIL', 'PROMOTIONAL_OFFER', 'Special offer! Get 30% off on your next order. Use code: SAVE30',
    'SENT', 'user5@example.com', '+919876543214', '30% Off - Limited Time Offer',
    0, 3, '2024-01-15 16:05:00', 'EMAIL_1705334700000_4567',
    '2024-01-15 16:00:00', '2024-01-15 16:05:00'
),
(
    2, 8001, 'PUSH', 'DELIVERY_ON_THE_WAY', 'Your order is on the way! Expected delivery in 10 minutes.',
    'PENDING', NULL, NULL, NULL,
    0, 3, NULL, NULL,
    '2024-01-15 17:00:00', '2024-01-15 17:00:00'
);

-- Insert some template-based notifications
UPDATE notifications SET 
    template_name = 'welcome_email',
    template_data = '{"userName":"John Doe","welcomeBonus":"50"}'
WHERE id = 1;

UPDATE notifications SET 
    template_name = 'order_confirmation',
    template_data = '{"orderNumber":"ORD001","totalAmount":"299.50","restaurantName":"Pizza Palace"}'
WHERE id = 2;

UPDATE notifications SET 
    template_name = 'payment_success',
    template_data = '{"amount":"450.75","paymentMethod":"UPI","transactionId":"TXN123456"}'
WHERE id = 4;

-- Insert some failed notifications with failure reasons
UPDATE notifications SET 
    failure_reason = 'Invalid phone number format',
    failed_at = '2024-01-15 15:10:00'
WHERE id = 8;

-- Insert some notifications with metadata
UPDATE notifications SET 
    metadata = '{"priority":"high","channel":"mobile_app","campaign_id":"welcome_2024"}'
WHERE id = 1;

UPDATE notifications SET 
    metadata = '{"priority":"normal","source":"order_service","restaurant_id":"101"}'
WHERE id = 2;

UPDATE notifications SET 
    metadata = '{"priority":"high","source":"payment_service","payment_id":"PAY_123"}'
WHERE id = 7;