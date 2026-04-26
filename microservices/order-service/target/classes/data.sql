-- Sample data for Order Service
-- This file is loaded on application startup

-- Sample orders for testing
INSERT INTO orders (order_number, user_id, restaurant_id, total_amount, order_status, payment_status, special_instructions, estimated_delivery_time, created_at, updated_at) 
SELECT 'ORD-20241127-143022-1001', 1, 1, 650.00, 'DELIVERED', 'COMPLETED', 'Extra spicy please', NOW() + INTERVAL '30 minutes', NOW() - INTERVAL '2 hours', NOW() - INTERVAL '30 minutes'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number = 'ORD-20241127-143022-1001');

INSERT INTO orders (order_number, user_id, restaurant_id, total_amount, order_status, payment_status, estimated_delivery_time, created_at, updated_at) 
SELECT 'ORD-20241127-143022-1002', 2, 2, 420.00, 'PREPARING', 'COMPLETED', NOW() + INTERVAL '25 minutes', NOW() - INTERVAL '15 minutes', NOW() - INTERVAL '5 minutes'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number = 'ORD-20241127-143022-1002');

INSERT INTO orders (order_number, user_id, restaurant_id, total_amount, order_status, payment_status, special_instructions, estimated_delivery_time, created_at, updated_at) 
SELECT 'ORD-20241127-143022-1003', 3, 3, 380.00, 'READY_FOR_PICKUP', 'COMPLETED', 'No onions', NOW() + INTERVAL '20 minutes', NOW() - INTERVAL '25 minutes', NOW() - INTERVAL '2 minutes'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number = 'ORD-20241127-143022-1003');

INSERT INTO orders (order_number, user_id, restaurant_id, total_amount, order_status, payment_status, delivery_partner_id, estimated_delivery_time, created_at, updated_at) 
SELECT 'ORD-20241127-143022-1004', 1, 4, 500.00, 'PICKED_UP', 'COMPLETED', 1, NOW() + INTERVAL '15 minutes', NOW() - INTERVAL '35 minutes', NOW() - INTERVAL '10 minutes'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number = 'ORD-20241127-143022-1004');

INSERT INTO orders (order_number, user_id, restaurant_id, total_amount, order_status, payment_status, cancellation_reason, cancelled_by, created_at, updated_at) 
SELECT 'ORD-20241127-143022-1005', 2, 1, 280.00, 'CANCELLED', 'REFUNDED', 'Changed mind', 'USER', NOW() - INTERVAL '1 hour', NOW() - INTERVAL '50 minutes'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number = 'ORD-20241127-143022-1005');

-- Order items for the first order (Spice Palace)
INSERT INTO order_items (order_id, menu_item_id, item_name, item_price, quantity, subtotal, special_instructions) 
SELECT 
    (SELECT id FROM orders WHERE order_number = 'ORD-20241127-143022-1001'),
    1, 'Butter Chicken', 320.00, 1, 320.00, 'Extra spicy'
WHERE NOT EXISTS (
    SELECT 1 FROM order_items oi 
    JOIN orders o ON oi.order_id = o.id 
    WHERE o.order_number = 'ORD-20241127-143022-1001' AND oi.menu_item_id = 1
);

INSERT INTO order_items (order_id, menu_item_id, item_name, item_price, quantity, subtotal) 
SELECT 
    (SELECT id FROM orders WHERE order_number = 'ORD-20241127-143022-1001'),
    4, 'Chicken Biryani', 380.00, 1, 380.00
WHERE NOT EXISTS (
    SELECT 1 FROM order_items oi 
    JOIN orders o ON oi.order_id = o.id 
    WHERE o.order_number = 'ORD-20241127-143022-1001' AND oi.menu_item_id = 4
);

-- Order items for the second order (Pizza Corner)
INSERT INTO order_items (order_id, menu_item_id, item_name, item_price, quantity, subtotal) 
SELECT 
    (SELECT id FROM orders WHERE order_number = 'ORD-20241127-143022-1002'),
    7, 'Margherita Pizza', 250.00, 1, 250.00
WHERE NOT EXISTS (
    SELECT 1 FROM order_items oi 
    JOIN orders o ON oi.order_id = o.id 
    WHERE o.order_number = 'ORD-20241127-143022-1002' AND oi.menu_item_id = 7
);

INSERT INTO order_items (order_id, menu_item_id, item_name, item_price, quantity, subtotal) 
SELECT 
    (SELECT id FROM orders WHERE order_number = 'ORD-20241127-143022-1002'),
    11, 'Garlic Bread', 120.00, 1, 120.00
WHERE NOT EXISTS (
    SELECT 1 FROM order_items oi 
    JOIN orders o ON oi.order_id = o.id 
    WHERE o.order_number = 'ORD-20241127-143022-1002' AND oi.menu_item_id = 11
);

INSERT INTO order_items (order_id, menu_item_id, item_name, item_price, quantity, subtotal) 
SELECT 
    (SELECT id FROM orders WHERE order_number = 'ORD-20241127-143022-1002'),
    10, 'Caesar Salad', 180.00, 1, 180.00
WHERE NOT EXISTS (
    SELECT 1 FROM order_items oi 
    JOIN orders o ON oi.order_id = o.id 
    WHERE o.order_number = 'ORD-20241127-143022-1002' AND oi.menu_item_id = 10
);

-- Order items for the third order (Burger King Express)
INSERT INTO order_items (order_id, menu_item_id, item_name, item_price, quantity, subtotal, special_instructions) 
SELECT 
    (SELECT id FROM orders WHERE order_number = 'ORD-20241127-143022-1003'),
    13, 'Whopper Burger', 220.00, 1, 220.00, 'No onions'
WHERE NOT EXISTS (
    SELECT 1 FROM order_items oi 
    JOIN orders o ON oi.order_id = o.id 
    WHERE o.order_number = 'ORD-20241127-143022-1003' AND oi.menu_item_id = 13
);

INSERT INTO order_items (order_id, menu_item_id, item_name, item_price, quantity, subtotal) 
SELECT 
    (SELECT id FROM orders WHERE order_number = 'ORD-20241127-143022-1003'),
    16, 'French Fries', 80.00, 2, 160.00
WHERE NOT EXISTS (
    SELECT 1 FROM order_items oi 
    JOIN orders o ON oi.order_id = o.id 
    WHERE o.order_number = 'ORD-20241127-143022-1003' AND oi.menu_item_id = 16
);

-- Order items for the fourth order (Dragon Wok)
INSERT INTO order_items (order_id, menu_item_id, item_name, item_price, quantity, subtotal) 
SELECT 
    (SELECT id FROM orders WHERE order_number = 'ORD-20241127-143022-1004'),
    18, 'Kung Pao Chicken', 280.00, 1, 280.00
WHERE NOT EXISTS (
    SELECT 1 FROM order_items oi 
    JOIN orders o ON oi.order_id = o.id 
    WHERE o.order_number = 'ORD-20241127-143022-1004' AND oi.menu_item_id = 18
);

INSERT INTO order_items (order_id, menu_item_id, item_name, item_price, quantity, subtotal) 
SELECT 
    (SELECT id FROM orders WHERE order_number = 'ORD-20241127-143022-1004'),
    19, 'Vegetable Fried Rice', 220.00, 1, 220.00
WHERE NOT EXISTS (
    SELECT 1 FROM order_items oi 
    JOIN orders o ON oi.order_id = o.id 
    WHERE o.order_number = 'ORD-20241127-143022-1004' AND oi.menu_item_id = 19
);

-- Order items for the cancelled order
INSERT INTO order_items (order_id, menu_item_id, item_name, item_price, quantity, subtotal) 
SELECT 
    (SELECT id FROM orders WHERE order_number = 'ORD-20241127-143022-1005'),
    2, 'Paneer Tikka Masala', 280.00, 1, 280.00
WHERE NOT EXISTS (
    SELECT 1 FROM order_items oi 
    JOIN orders o ON oi.order_id = o.id 
    WHERE o.order_number = 'ORD-20241127-143022-1005' AND oi.menu_item_id = 2
);

-- Additional recent orders for testing pagination
INSERT INTO orders (order_number, user_id, restaurant_id, total_amount, order_status, payment_status, estimated_delivery_time, created_at, updated_at) 
SELECT 'ORD-20241127-143022-1006', 1, 5, 340.00, 'PLACED', 'PENDING', NOW() + INTERVAL '40 minutes', NOW() - INTERVAL '5 minutes', NOW() - INTERVAL '5 minutes'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number = 'ORD-20241127-143022-1006');

INSERT INTO order_items (order_id, menu_item_id, item_name, item_price, quantity, subtotal) 
SELECT 
    (SELECT id FROM orders WHERE order_number = 'ORD-20241127-143022-1006'),
    23, 'Chicken Quesadilla', 200.00, 1, 200.00
WHERE NOT EXISTS (
    SELECT 1 FROM order_items oi 
    JOIN orders o ON oi.order_id = o.id 
    WHERE o.order_number = 'ORD-20241127-143022-1006' AND oi.menu_item_id = 23
);

INSERT INTO order_items (order_id, menu_item_id, item_name, item_price, quantity, subtotal) 
SELECT 
    (SELECT id FROM orders WHERE order_number = 'ORD-20241127-143022-1006'),
    26, 'Nachos Supreme', 140.00, 1, 140.00
WHERE NOT EXISTS (
    SELECT 1 FROM order_items oi 
    JOIN orders o ON oi.order_id = o.id 
    WHERE o.order_number = 'ORD-20241127-143022-1006' AND oi.menu_item_id = 26
);

INSERT INTO orders (order_number, user_id, restaurant_id, total_amount, order_status, payment_status, estimated_delivery_time, created_at, updated_at) 
SELECT 'ORD-20241127-143022-1007', 3, 2, 570.00, 'ACCEPTED', 'COMPLETED', NOW() + INTERVAL '30 minutes', NOW() - INTERVAL '10 minutes', NOW() - INTERVAL '8 minutes'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number = 'ORD-20241127-143022-1007');

INSERT INTO order_items (order_id, menu_item_id, item_name, item_price, quantity, subtotal) 
SELECT 
    (SELECT id FROM orders WHERE order_number = 'ORD-20241127-143022-1007'),
    8, 'Pepperoni Pizza', 320.00, 1, 320.00
WHERE NOT EXISTS (
    SELECT 1 FROM order_items oi 
    JOIN orders o ON oi.order_id = o.id 
    WHERE o.order_number = 'ORD-20241127-143022-1007' AND oi.menu_item_id = 8
);

INSERT INTO order_items (order_id, menu_item_id, item_name, item_price, quantity, subtotal) 
SELECT 
    (SELECT id FROM orders WHERE order_number = 'ORD-20241127-143022-1007'),
    9, 'Chicken Alfredo Pasta', 280.00, 1, 280.00
WHERE NOT EXISTS (
    SELECT 1 FROM order_items oi 
    JOIN orders o ON oi.order_id = o.id 
    WHERE o.order_number = 'ORD-20241127-143022-1007' AND oi.menu_item_id = 9
);

-- Note: Menu item IDs correspond to the menu items created in the menu-service data.sql