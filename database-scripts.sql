-- ============================================================================
-- FOOD DELIVERY PLATFORM - DATABASE SCRIPTS
-- ============================================================================
-- This file contains all database setup and maintenance scripts
-- ============================================================================

-- ============================================================================
-- SECTION 1: DATABASE SETUP
-- ============================================================================

-- Create main database
CREATE DATABASE IF NOT EXISTS fooddelivery;

-- Connect to the database
\c fooddelivery;

-- ============================================================================
-- SECTION 2: PASSWORD FIX (WORKING SOLUTION)
-- ============================================================================
-- Use this script if login fails with "Invalid email or password"
-- This uses a BCrypt hash that is VERIFIED to work with the application
-- Password for all users: password123
-- Hash: $2a$10$RJKu31UxBaEI0OAnbvTKCOybjm7IVYB18nczxZlvondu315VCxelC
-- ============================================================================

-- Update all test users with the WORKING BCrypt hash
UPDATE users 
SET password = '$2a$10$RJKu31UxBaEI0OAnbvTKCOybjm7IVYB18nczxZlvondu315VCxelC'
WHERE email IN (
    'admin@fooddelivery.com',
    'raj@spicepalace.com',
    'priya@pizzacorner.com',
    'ahmed@burgerking.com',
    'ravi.delivery@gmail.com',
    'suresh.delivery@gmail.com',
    'vikram.delivery@gmail.com',
    'john.doe@gmail.com',
    'jane.smith@gmail.com',
    'mike.johnson@gmail.com'
);

-- ============================================================================
-- SECTION 3: VERIFICATION QUERIES
-- ============================================================================

-- Verify password update
SELECT 
    id,
    name,
    email,
    role,
    is_active,
    LEFT(password, 30) as password_hash,
    CASE 
        WHEN password = '$2a$10$RJKu31UxBaEI0OAnbvTKCOybjm7IVYB18nczxZlvondu315VCxelC' THEN '✓ CORRECT'
        ELSE '✗ WRONG'
    END as status
FROM users
WHERE deleted_at IS NULL
ORDER BY role, email;

-- Check all users
SELECT 
    id,
    name,
    email,
    role,
    is_active,
    is_verified,
    city,
    created_at
FROM users
WHERE deleted_at IS NULL
ORDER BY role, email;

-- Check user count by role
SELECT 
    role,
    COUNT(*) as user_count,
    COUNT(CASE WHEN is_active = true THEN 1 END) as active_count
FROM users
WHERE deleted_at IS NULL
GROUP BY role
ORDER BY role;

-- ============================================================================
-- SECTION 4: TEST USER CREDENTIALS
-- ============================================================================
-- All users have password: password123
--
-- PLATFORM_ADMIN:
--   Email: admin@fooddelivery.com
--   Phone: 9999999999
--
-- RESTAURANT_ADMIN:
--   Email: raj@spicepalace.com (Spice Palace)
--   Phone: 9876543210
--
--   Email: priya@pizzacorner.com (Pizza Corner)
--   Phone: 9876543211
--
--   Email: ahmed@burgerking.com (Burger King)
--   Phone: 9876543212
--
-- DELIVERY_PARTNER:
--   Email: ravi.delivery@gmail.com
--   Phone: 9876543213
--
--   Email: suresh.delivery@gmail.com
--   Phone: 9876543214
--
--   Email: vikram.delivery@gmail.com
--   Phone: 9876543215
--
-- CUSTOMER:
--   Email: john.doe@gmail.com
--   Phone: 9876543216
--
--   Email: jane.smith@gmail.com
--   Phone: 9876543217
--
--   Email: mike.johnson@gmail.com
--   Phone: 9876543218
--
--   Email: anush@example.com
--   Phone: 9876543219
-- ============================================================================

-- ============================================================================
-- SECTION 5: USEFUL MAINTENANCE QUERIES
-- ============================================================================

-- View all restaurants
SELECT 
    id,
    name,
    cuisine_type,
    rating,
    is_active,
    is_approved,
    is_open,
    owner_id
FROM restaurants
WHERE deleted_at IS NULL
ORDER BY name;

-- View all orders
SELECT 
    o.id,
    o.order_number,
    o.status,
    o.total_amount,
    u.name as customer_name,
    r.name as restaurant_name,
    o.created_at
FROM orders o
JOIN users u ON o.customer_id = u.id
JOIN restaurants r ON o.restaurant_id = r.id
ORDER BY o.created_at DESC
LIMIT 20;

-- View menu items by restaurant
SELECT 
    mi.id,
    mi.name,
    mi.price,
    mi.category,
    mi.is_available,
    r.name as restaurant_name
FROM menu_items mi
JOIN restaurants r ON mi.restaurant_id = r.id
WHERE mi.deleted_at IS NULL
ORDER BY r.name, mi.category, mi.name;

-- ============================================================================
-- SECTION 6: MICROSERVICES SETUP (Optional)
-- ============================================================================
-- Uncomment if using microservices architecture

-- CREATE DATABASE IF NOT EXISTS auth_service_db;
-- CREATE DATABASE IF NOT EXISTS restaurant_service_db;
-- CREATE DATABASE IF NOT EXISTS menu_service_db;
-- CREATE DATABASE IF NOT EXISTS order_service_db;
-- CREATE DATABASE IF NOT EXISTS payment_service_db;
-- CREATE DATABASE IF NOT EXISTS delivery_service_db;
-- CREATE DATABASE IF NOT EXISTS notification_service_db;

-- Create dedicated user (optional)
-- CREATE USER fooddelivery_user WITH PASSWORD 'fooddelivery_pass';
-- GRANT ALL PRIVILEGES ON DATABASE fooddelivery TO fooddelivery_user;

-- ============================================================================
-- SECTION 7: QUICK FIXES
-- ============================================================================

-- Reset all user passwords to 'password123'
-- UPDATE users 
-- SET password = '$2a$10$RJKu31UxBaEI0OAnbvTKCOybjm7IVYB18nczxZlvondu315VCxelC'
-- WHERE deleted_at IS NULL;

-- Activate all users
-- UPDATE users SET is_active = true WHERE deleted_at IS NULL;

-- Verify all users
-- UPDATE users SET is_verified = true WHERE deleted_at IS NULL;

-- Approve all restaurants
-- UPDATE restaurants SET is_approved = true WHERE deleted_at IS NULL;

-- Open all restaurants
-- UPDATE restaurants SET is_open = true WHERE deleted_at IS NULL;

-- ============================================================================
-- SECTION 8: CLEANUP (Use with caution)
-- ============================================================================

-- Delete all orders (CAUTION: This will delete all order data)
-- DELETE FROM order_items;
-- DELETE FROM payments;
-- DELETE FROM deliveries;
-- DELETE FROM orders;

-- Reset auto-increment sequences
-- ALTER SEQUENCE users_id_seq RESTART WITH 1;
-- ALTER SEQUENCE restaurants_id_seq RESTART WITH 1;
-- ALTER SEQUENCE menu_items_id_seq RESTART WITH 1;
-- ALTER SEQUENCE orders_id_seq RESTART WITH 1;

-- ============================================================================
-- END OF DATABASE SCRIPTS
-- ============================================================================
-- 
-- USAGE INSTRUCTIONS:
-- 
-- 1. To create database:
--    psql -U postgres -f database-scripts.sql
--
-- 2. To fix passwords only:
--    psql -U postgres -d fooddelivery -c "UPDATE users SET password = '$2a$10$RJKu31UxBaEI0OAnbvTKCOybjm7IVYB18nczxZlvondu315VCxelC' WHERE deleted_at IS NULL;"
--
-- 3. To verify users:
--    psql -U postgres -d fooddelivery -c "SELECT email, role, is_active FROM users WHERE deleted_at IS NULL;"
--
-- ============================================================================
