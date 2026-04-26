-- Sample data for Delivery Service
-- This file will be executed on application startup if spring.jpa.hibernate.ddl-auto is set to create or create-drop

-- Insert sample delivery partners
INSERT INTO delivery_partners (
    name, phone, vehicle_type, is_active, is_available, 
    current_latitude, current_longitude, rating, total_deliveries, successful_deliveries,
    last_active_at, created_at, updated_at
) VALUES 
(
    'Rajesh Kumar', '+919876543210', 'MOTORCYCLE', true, true,
    12.9716, 77.5946, 4.5, 150, 142,
    '2024-01-15 10:30:00', '2024-01-01 09:00:00', '2024-01-15 10:30:00'
),
(
    'Amit Singh', '+919876543211', 'SCOOTER', true, true,
    12.9352, 77.6245, 4.2, 89, 84,
    '2024-01-15 10:25:00', '2024-01-02 10:00:00', '2024-01-15 10:25:00'
),
(
    'Priya Sharma', '+919876543212', 'BICYCLE', true, false,
    12.9698, 77.7500, 4.8, 67, 66,
    '2024-01-15 09:45:00', '2024-01-03 11:00:00', '2024-01-15 09:45:00'
),
(
    'Suresh Reddy', '+919876543213', 'CAR', true, true,
    12.9141, 77.6101, 4.3, 203, 195,
    '2024-01-15 10:20:00', '2024-01-04 08:30:00', '2024-01-15 10:20:00'
),
(
    'Deepak Gupta', '+919876543214', 'MOTORCYCLE', true, true,
    12.9279, 77.6271, 4.6, 178, 171,
    '2024-01-15 10:35:00', '2024-01-05 09:15:00', '2024-01-15 10:35:00'
),
(
    'Anita Patel', '+919876543215', 'SCOOTER', false, false,
    12.9165, 77.6101, 3.9, 45, 40,
    '2024-01-14 18:00:00', '2024-01-06 12:00:00', '2024-01-14 18:00:00'
);

-- Insert sample deliveries
INSERT INTO deliveries (
    order_id, delivery_partner_id, delivery_status, assigned_at, accepted_at, 
    picked_up_at, on_the_way_at, delivered_at, estimated_delivery_time, 
    actual_delivery_time_minutes, rejection_count, customer_rating, 
    customer_feedback, created_at, updated_at
) VALUES 
(
    1, 1, 'DELIVERED', '2024-01-15 09:00:00', '2024-01-15 09:02:00',
    '2024-01-15 09:25:00', '2024-01-15 09:30:00', '2024-01-15 09:45:00',
    '2024-01-15 09:32:00', 45, 0, 5,
    'Excellent delivery! Very fast and food was hot.', 
    '2024-01-15 09:00:00', '2024-01-15 09:45:00'
),
(
    2, 2, 'DELIVERED', '2024-01-15 10:00:00', '2024-01-15 10:01:00',
    '2024-01-15 10:20:00', '2024-01-15 10:25:00', '2024-01-15 10:40:00',
    '2024-01-15 10:31:00', 40, 0, 4,
    'Good service, delivery partner was polite.',
    '2024-01-15 10:00:00', '2024-01-15 10:40:00'
),
(
    3, 4, 'ON_THE_WAY', '2024-01-15 10:15:00', '2024-01-15 10:16:00',
    '2024-01-15 10:35:00', '2024-01-15 10:40:00', NULL,
    '2024-01-15 10:46:00', NULL, 0, NULL, NULL,
    '2024-01-15 10:15:00', '2024-01-15 10:40:00'
),
(
    4, 5, 'ACCEPTED', '2024-01-15 10:30:00', '2024-01-15 10:31:00',
    NULL, NULL, NULL, '2024-01-15 11:01:00', NULL, 0, NULL, NULL,
    '2024-01-15 10:30:00', '2024-01-15 10:31:00'
),
(
    5, 1, 'ASSIGNED', '2024-01-15 10:45:00', NULL,
    NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL,
    '2024-01-15 10:45:00', '2024-01-15 10:45:00'
),
(
    6, 2, 'CANCELLED', '2024-01-15 08:00:00', '2024-01-15 08:01:00',
    NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL,
    '2024-01-15 08:00:00', '2024-01-15 08:15:00'
);

-- Update delivery notes for some deliveries
UPDATE deliveries SET delivery_notes = 'Customer requested contactless delivery' WHERE id = 1;
UPDATE deliveries SET delivery_notes = 'Delivered to security guard as customer was not available' WHERE id = 2;
UPDATE deliveries SET delivery_notes = 'Customer called to confirm address' WHERE id = 3;
UPDATE deliveries SET cancellation_reason = 'Restaurant was closed' WHERE id = 6;