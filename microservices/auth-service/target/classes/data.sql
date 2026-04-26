-- Sample data for Auth Service (PostgreSQL compatible)
-- This file is loaded on application startup

-- Demo users for frontend testing (passwords are 'password123' encoded with BCrypt)
INSERT INTO users (name, email, phone, password, role, is_active, is_verified, is_email_verified, is_phone_verified, failed_login_attempts, created_at, updated_at) 
VALUES ('John Demo', 'john.demo@fooddelivery.com', '9876543201', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'CUSTOMER', true, true, true, true, 0, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (name, email, phone, password, role, is_active, is_verified, is_email_verified, is_phone_verified, failed_login_attempts, created_at, updated_at) 
VALUES ('Restaurant Demo', 'restaurant.demo@fooddelivery.com', '9876543202', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'RESTAURANT_ADMIN', true, true, true, true, 0, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (name, email, phone, password, role, is_active, is_verified, is_email_verified, is_phone_verified, failed_login_attempts, created_at, updated_at) 
VALUES ('Delivery Demo', 'delivery.demo@fooddelivery.com', '9876543203', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'DELIVERY_PARTNER', true, true, true, true, 0, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (name, email, phone, password, role, is_active, is_verified, is_email_verified, is_phone_verified, failed_login_attempts, created_at, updated_at) 
VALUES ('Admin Demo', 'admin.demo@fooddelivery.com', '9876543204', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'PLATFORM_ADMIN', true, true, true, true, 0, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Additional sample users
INSERT INTO users (name, email, phone, password, role, is_active, is_verified, is_email_verified, is_phone_verified, failed_login_attempts, created_at, updated_at) 
VALUES ('Admin User', 'admin@fooddelivery.com', '9999999999', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'PLATFORM_ADMIN', true, true, true, true, 0, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Restaurant Admins
INSERT INTO users (name, email, phone, password, role, is_active, is_verified, is_email_verified, is_phone_verified, failed_login_attempts, created_at, updated_at) 
VALUES ('Raj Kumar', 'raj@spicepalace.com', '9876543210', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'RESTAURANT_ADMIN', true, true, true, true, 0, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (name, email, phone, password, role, is_active, is_verified, is_email_verified, is_phone_verified, failed_login_attempts, created_at, updated_at) 
VALUES ('Priya Sharma', 'priya@pizzacorner.com', '9876543211', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'RESTAURANT_ADMIN', true, true, true, true, 0, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (name, email, phone, password, role, is_active, is_verified, is_email_verified, is_phone_verified, failed_login_attempts, created_at, updated_at) 
VALUES ('Ahmed Ali', 'ahmed@burgerking.com', '9876543212', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'RESTAURANT_ADMIN', true, true, true, true, 0, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Delivery Partners
INSERT INTO users (name, email, phone, password, role, is_active, is_verified, is_email_verified, is_phone_verified, failed_login_attempts, created_at, updated_at) 
VALUES ('Ravi Kumar', 'ravi.delivery@gmail.com', '9876543213', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'DELIVERY_PARTNER', true, true, true, true, 0, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (name, email, phone, password, role, is_active, is_verified, is_email_verified, is_phone_verified, failed_login_attempts, created_at, updated_at) 
VALUES ('Suresh Babu', 'suresh.delivery@gmail.com', '9876543214', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'DELIVERY_PARTNER', true, true, true, true, 0, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (name, email, phone, password, role, is_active, is_verified, is_email_verified, is_phone_verified, failed_login_attempts, created_at, updated_at) 
VALUES ('Vikram Singh', 'vikram.delivery@gmail.com', '9876543215', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'DELIVERY_PARTNER', true, true, true, true, 0, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Customers
INSERT INTO users (name, email, phone, password, role, is_active, is_verified, is_email_verified, is_phone_verified, failed_login_attempts, created_at, updated_at) 
VALUES ('John Doe', 'john.doe@gmail.com', '9876543216', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'CUSTOMER', true, true, true, true, 0, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (name, email, phone, password, role, is_active, is_verified, is_email_verified, is_phone_verified, failed_login_attempts, created_at, updated_at) 
VALUES ('Jane Smith', 'jane.smith@gmail.com', '9876543217', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'CUSTOMER', true, true, true, true, 0, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (name, email, phone, password, role, is_active, is_verified, is_email_verified, is_phone_verified, failed_login_attempts, created_at, updated_at) 
VALUES ('Mike Johnson', 'mike.johnson@gmail.com', '9876543218', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'CUSTOMER', true, true, true, true, 0, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Note: The BCrypt hash '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa' represents 'password123'