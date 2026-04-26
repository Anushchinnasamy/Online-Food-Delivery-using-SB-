-- Sample data for Restaurant Service
-- This file is loaded on application startup

-- Insert sample restaurants with proper image URLs and active status
INSERT INTO restaurants (name, description, address, city, pincode, latitude, longitude, phone, email, cuisine_type, image_url, rating, total_reviews, delivery_time_minutes, minimum_order_amount, delivery_fee, is_active, is_approved, is_open, opening_time, closing_time, created_at, updated_at) 
SELECT 'Spice Palace', 'Authentic Indian cuisine with traditional flavors', '123 MG Road, Brigade Road', 'Bangalore', '560001', 12.9716, 77.5946, '9876543210', 'raj@spicepalace.com', 'Indian', 'https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=400&h=300&fit=crop', 4.5, 150, 30, 200.00, 25.00, true, true, true, '10:00:00', '23:00:00', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM restaurants WHERE email = 'raj@spicepalace.com');

INSERT INTO restaurants (name, description, address, city, pincode, latitude, longitude, phone, email, cuisine_type, image_url, rating, total_reviews, delivery_time_minutes, minimum_order_amount, delivery_fee, is_active, is_approved, is_open, opening_time, closing_time, created_at, updated_at) 
SELECT 'Pizza Corner', 'Wood-fired pizzas and Italian delicacies', '456 Commercial Street', 'Bangalore', '560002', 12.9698, 77.6103, '9876543211', 'priya@pizzacorner.com', 'Italian', 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400&h=300&fit=crop', 4.2, 89, 25, 150.00, 20.00, true, true, true, '11:00:00', '23:30:00', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM restaurants WHERE email = 'priya@pizzacorner.com');

INSERT INTO restaurants (name, description, address, city, pincode, latitude, longitude, phone, email, cuisine_type, image_url, rating, total_reviews, delivery_time_minutes, minimum_order_amount, delivery_fee, is_active, is_approved, is_open, opening_time, closing_time, created_at, updated_at) 
SELECT 'Burger King Express', 'Fast food burgers and fries', '789 Koramangala', 'Bangalore', '560034', 12.9352, 77.6245, '9876543212', 'ahmed@burgerking.com', 'Fast Food', 'https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=400&h=300&fit=crop', 3.8, 234, 20, 100.00, 15.00, true, true, true, '09:00:00', '01:00:00', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM restaurants WHERE email = 'ahmed@burgerking.com');

INSERT INTO restaurants (name, description, address, city, pincode, latitude, longitude, phone, email, cuisine_type, image_url, rating, total_reviews, delivery_time_minutes, minimum_order_amount, delivery_fee, is_active, is_approved, is_open, opening_time, closing_time, created_at, updated_at) 
SELECT 'Dragon Wok', 'Authentic Chinese and Asian cuisine', '321 Indiranagar', 'Bangalore', '560038', 12.9719, 77.6412, '9876543213', 'chef@dragonwok.com', 'Chinese', 'https://images.unsplash.com/photo-1617093727343-374698b1b08d?w=400&h=300&fit=crop', 4.1, 67, 35, 180.00, 30.00, true, true, true, '12:00:00', '22:30:00', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM restaurants WHERE email = 'chef@dragonwok.com');

INSERT INTO restaurants (name, description, address, city, pincode, latitude, longitude, phone, email, cuisine_type, image_url, rating, total_reviews, delivery_time_minutes, minimum_order_amount, delivery_fee, is_active, is_approved, is_open, opening_time, closing_time, created_at, updated_at) 
SELECT 'Taco Bell', 'Mexican fast food and tacos', '654 Whitefield', 'Bangalore', '560066', 12.9698, 77.7500, '9876543214', 'manager@tacobell.com', 'Mexican', 'https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400&h=300&fit=crop', 3.9, 112, 22, 120.00, 18.00, true, true, true, '10:30:00', '23:00:00', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM restaurants WHERE email = 'manager@tacobell.com');

-- Mumbai restaurants
INSERT INTO restaurants (name, description, address, city, pincode, latitude, longitude, phone, email, cuisine_type, image_url, rating, total_reviews, delivery_time_minutes, minimum_order_amount, delivery_fee, is_active, is_approved, is_open, opening_time, closing_time, created_at, updated_at) 
SELECT 'Mumbai Darbar', 'Traditional Maharashtrian cuisine', '123 Linking Road, Bandra', 'Mumbai', '400050', 19.0596, 72.8295, '9876543215', 'owner@mumbaidarbar.com', 'Indian', 'https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=400&h=300&fit=crop', 4.3, 198, 28, 250.00, 35.00, true, true, true, '11:00:00', '23:30:00', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM restaurants WHERE email = 'owner@mumbaidarbar.com');

INSERT INTO restaurants (name, description, address, city, pincode, latitude, longitude, phone, email, cuisine_type, image_url, rating, total_reviews, delivery_time_minutes, minimum_order_amount, delivery_fee, is_active, is_approved, is_open, opening_time, closing_time, created_at, updated_at) 
SELECT 'Coastal Kitchen', 'Fresh seafood and coastal delicacies', '456 Marine Drive', 'Mumbai', '400020', 18.9435, 72.8234, '9876543216', 'chef@coastalkitchen.com', 'Seafood', 'https://images.unsplash.com/photo-1559847844-d721426d6edc?w=400&h=300&fit=crop', 4.6, 87, 40, 300.00, 40.00, true, true, true, '12:00:00', '22:00:00', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM restaurants WHERE email = 'chef@coastalkitchen.com');

-- Delhi restaurants
INSERT INTO restaurants (name, description, address, city, pincode, latitude, longitude, phone, email, cuisine_type, image_url, rating, total_reviews, delivery_time_minutes, minimum_order_amount, delivery_fee, is_active, is_approved, is_open, opening_time, closing_time, created_at, updated_at) 
SELECT 'Delhi Delights', 'North Indian and Mughlai cuisine', '789 Connaught Place', 'Delhi', '110001', 28.6315, 77.2167, '9876543217', 'manager@delhidelights.com', 'North Indian', 'https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=400&h=300&fit=crop', 4.4, 156, 32, 220.00, 28.00, true, true, true, '10:00:00', '23:00:00', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM restaurants WHERE email = 'manager@delhidelights.com');

-- Pending approval restaurants (some closed for variety)
INSERT INTO restaurants (name, description, address, city, pincode, latitude, longitude, phone, email, cuisine_type, image_url, rating, total_reviews, delivery_time_minutes, minimum_order_amount, delivery_fee, is_active, is_approved, is_open, opening_time, closing_time, created_at, updated_at) 
SELECT 'New Taste Hub', 'Multi-cuisine restaurant', '999 HSR Layout', 'Bangalore', '560102', 12.9082, 77.6476, '9876543218', 'owner@newtastehub.com', 'Multi-Cuisine', 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400&h=300&fit=crop', 4.0, 25, 25, 150.00, 20.00, true, true, false, '09:00:00', '22:00:00', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM restaurants WHERE email = 'owner@newtastehub.com');

INSERT INTO restaurants (name, description, address, city, pincode, latitude, longitude, phone, email, cuisine_type, image_url, rating, total_reviews, delivery_time_minutes, minimum_order_amount, delivery_fee, is_active, is_approved, is_open, opening_time, closing_time, created_at, updated_at) 
SELECT 'Fresh Bites', 'Healthy salads and wraps', '111 Electronic City', 'Bangalore', '560100', 12.8456, 77.6603, '9876543219', 'health@freshbites.com', 'Healthy', 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400&h=300&fit=crop', 4.2, 45, 20, 100.00, 15.00, true, true, true, '08:00:00', '21:00:00', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM restaurants WHERE email = 'health@freshbites.com');