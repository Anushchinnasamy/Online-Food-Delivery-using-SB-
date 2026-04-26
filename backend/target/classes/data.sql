-- Sample data for Food Delivery Platform
-- This file is loaded on application startup

-- Insert sample users (passwords are 'password123' encoded with BCrypt)
-- Using INSERT ... ON CONFLICT to handle existing data
INSERT INTO users (name, email, phone, password, role, is_active, is_verified, address, city, pincode, latitude, longitude, created_at, updated_at) 
SELECT 'Admin User', 'admin@fooddelivery.com', '9999999999', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'PLATFORM_ADMIN', true, true, 'Admin Office, Tech Park', 'Bangalore', '560001', 12.9716, 77.5946, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@fooddelivery.com');

INSERT INTO users (name, email, phone, password, role, is_active, is_verified, address, city, pincode, latitude, longitude, created_at, updated_at) 
SELECT 'Raj Kumar', 'raj@spicepalace.com', '9876543210', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'RESTAURANT_ADMIN', true, true, 'MG Road', 'Bangalore', '560001', 12.9716, 77.5946, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'raj@spicepalace.com');

INSERT INTO users (name, email, phone, password, role, is_active, is_verified, address, city, pincode, latitude, longitude, created_at, updated_at) 
SELECT 'Priya Sharma', 'priya@pizzacorner.com', '9876543211', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'RESTAURANT_ADMIN', true, true, 'Koramangala', 'Bangalore', '560034', 12.9352, 77.6245, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'priya@pizzacorner.com');

INSERT INTO users (name, email, phone, password, role, is_active, is_verified, address, city, pincode, latitude, longitude, created_at, updated_at) 
SELECT 'Ahmed Ali', 'ahmed@burgerking.com', '9876543212', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'RESTAURANT_ADMIN', true, true, 'Indiranagar', 'Bangalore', '560038', 12.9719, 77.6412, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'ahmed@burgerking.com');

INSERT INTO users (name, email, phone, password, role, is_active, is_verified, address, city, pincode, latitude, longitude, created_at, updated_at) 
SELECT 'Ravi Kumar', 'ravi.delivery@gmail.com', '9876543213', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'DELIVERY_PARTNER', true, true, 'Whitefield', 'Bangalore', '560066', 12.9698, 77.7500, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'ravi.delivery@gmail.com');

INSERT INTO users (name, email, phone, password, role, is_active, is_verified, address, city, pincode, latitude, longitude, created_at, updated_at) 
SELECT 'Suresh Babu', 'suresh.delivery@gmail.com', '9876543214', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'DELIVERY_PARTNER', true, true, 'Electronic City', 'Bangalore', '560100', 12.8456, 77.6603, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'suresh.delivery@gmail.com');

INSERT INTO users (name, email, phone, password, role, is_active, is_verified, address, city, pincode, latitude, longitude, created_at, updated_at) 
SELECT 'Vikram Singh', 'vikram.delivery@gmail.com', '9876543215', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'DELIVERY_PARTNER', true, true, 'Jayanagar', 'Bangalore', '560011', 12.9279, 77.5937, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'vikram.delivery@gmail.com');

INSERT INTO users (name, email, phone, password, role, is_active, is_verified, address, city, pincode, latitude, longitude, created_at, updated_at) 
SELECT 'John Doe', 'john.doe@gmail.com', '9876543216', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'CUSTOMER', true, true, '123 Main Street, HSR Layout', 'Bangalore', '560102', 12.9082, 77.6476, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'john.doe@gmail.com');

INSERT INTO users (name, email, phone, password, role, is_active, is_verified, address, city, pincode, latitude, longitude, created_at, updated_at) 
SELECT 'Jane Smith', 'jane.smith@gmail.com', '9876543217', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'CUSTOMER', true, true, '456 Park Avenue, BTM Layout', 'Bangalore', '560076', 12.9165, 77.6101, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'jane.smith@gmail.com');

INSERT INTO users (name, email, phone, password, role, is_active, is_verified, address, city, pincode, latitude, longitude, created_at, updated_at) 
SELECT 'Mike Johnson', 'mike.johnson@gmail.com', '9876543218', '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa', 'CUSTOMER', true, true, '789 Oak Street, Marathahalli', 'Bangalore', '560037', 12.9591, 77.6974, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'mike.johnson@gmail.com');

-- Insert sample restaurants
INSERT INTO restaurants (name, description, address, city, pincode, latitude, longitude, phone, email, cuisine_type, image_url, rating, total_reviews, delivery_time_minutes, minimum_order_amount, delivery_fee, is_active, is_approved, is_open, opening_time, closing_time, owner_id, created_at, updated_at) 
SELECT 'Spice Palace', 'Authentic Indian cuisine with traditional flavors', 'MG Road, Brigade Road', 'Bangalore', '560001', 12.9716, 77.5946, '9876543210', 'orders@spicepalace.com', 'Indian', 'https://images.unsplash.com/photo-1585937421612-70a008356fbe', 4.5, 150, 35, 200.00, 30.00, true, true, true, '09:00:00', '23:00:00', (SELECT id FROM users WHERE email = 'raj@spicepalace.com'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM restaurants WHERE phone = '9876543210');

INSERT INTO restaurants (name, description, address, city, pincode, latitude, longitude, phone, email, cuisine_type, image_url, rating, total_reviews, delivery_time_minutes, minimum_order_amount, delivery_fee, is_active, is_approved, is_open, opening_time, closing_time, owner_id, created_at, updated_at) 
SELECT 'Pizza Corner', 'Fresh wood-fired pizzas and Italian delights', 'Koramangala 5th Block', 'Bangalore', '560034', 12.9352, 77.6245, '9876543211', 'orders@pizzacorner.com', 'Italian', 'https://images.unsplash.com/photo-1513104890138-7c749659a591', 4.2, 89, 25, 150.00, 25.00, true, true, true, '11:00:00', '23:30:00', (SELECT id FROM users WHERE email = 'priya@pizzacorner.com'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM restaurants WHERE phone = '9876543211');

INSERT INTO restaurants (name, description, address, city, pincode, latitude, longitude, phone, email, cuisine_type, image_url, rating, total_reviews, delivery_time_minutes, minimum_order_amount, delivery_fee, is_active, is_approved, is_open, opening_time, closing_time, owner_id, created_at, updated_at) 
SELECT 'Burger King', 'Flame-grilled burgers and fast food favorites', 'Indiranagar 100 Feet Road', 'Bangalore', '560038', 12.9719, 77.6412, '9876543212', 'orders@burgerking.com', 'Fast Food', 'https://images.unsplash.com/photo-1571091718767-18b5b1457add', 4.0, 200, 20, 100.00, 20.00, true, true, true, '10:00:00', '24:00:00', (SELECT id FROM users WHERE email = 'ahmed@burgerking.com'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM restaurants WHERE phone = '9876543212');

-- Insert sample menu items
INSERT INTO menu_items (name, description, price, category, image_url, is_vegetarian, is_vegan, is_spicy, is_available, preparation_time_minutes, ingredients, calories_per_serving, serving_size, restaurant_id, created_at, updated_at) 
SELECT 'Butter Chicken', 'Creamy tomato-based curry with tender chicken pieces', 320.00, 'Main Course', 'https://images.unsplash.com/photo-1588166524941-3bf61a9c41db', false, false, true, true, 20, 'Chicken, Tomatoes, Cream, Spices', 450, '1 serving', (SELECT id FROM restaurants WHERE name = 'Spice Palace'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Butter Chicken' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Spice Palace'));

INSERT INTO menu_items (name, description, price, category, image_url, is_vegetarian, is_vegan, is_spicy, is_available, preparation_time_minutes, ingredients, calories_per_serving, serving_size, restaurant_id, created_at, updated_at) 
SELECT 'Paneer Tikka Masala', 'Grilled cottage cheese in rich spiced gravy', 280.00, 'Main Course', 'https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8', true, false, true, true, 18, 'Paneer, Onions, Tomatoes, Spices', 380, '1 serving', (SELECT id FROM restaurants WHERE name = 'Spice Palace'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Paneer Tikka Masala' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Spice Palace'));

INSERT INTO menu_items (name, description, price, category, image_url, is_vegetarian, is_vegan, is_spicy, is_available, preparation_time_minutes, ingredients, calories_per_serving, serving_size, restaurant_id, created_at, updated_at) 
SELECT 'Biryani', 'Fragrant basmati rice with spiced meat/vegetables', 350.00, 'Rice', 'https://images.unsplash.com/photo-1563379091339-03246963d96c', false, false, true, true, 25, 'Basmati Rice, Chicken, Spices, Saffron', 520, '1 serving', (SELECT id FROM restaurants WHERE name = 'Spice Palace'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Biryani' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Spice Palace'));

INSERT INTO menu_items (name, description, price, category, image_url, is_vegetarian, is_vegan, is_spicy, is_available, preparation_time_minutes, ingredients, calories_per_serving, serving_size, restaurant_id, created_at, updated_at) 
SELECT 'Naan Bread', 'Fresh baked Indian flatbread', 60.00, 'Bread', 'https://images.unsplash.com/photo-1601050690597-df0568f70950', true, false, false, true, 10, 'Flour, Yogurt, Yeast', 180, '2 pieces', (SELECT id FROM restaurants WHERE name = 'Spice Palace'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Naan Bread' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Spice Palace'));

INSERT INTO menu_items (name, description, price, category, image_url, is_vegetarian, is_vegan, is_spicy, is_available, preparation_time_minutes, ingredients, calories_per_serving, serving_size, restaurant_id, created_at, updated_at) 
SELECT 'Mango Lassi', 'Refreshing yogurt drink with mango', 80.00, 'Beverages', 'https://images.unsplash.com/photo-1571197119282-7c4e2b2d7c6e', true, false, false, true, 5, 'Yogurt, Mango, Sugar', 150, '1 glass', (SELECT id FROM restaurants WHERE name = 'Spice Palace'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Mango Lassi' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Spice Palace'));

-- Pizza Corner Menu
INSERT INTO menu_items (name, description, price, category, image_url, is_vegetarian, is_vegan, is_spicy, is_available, preparation_time_minutes, ingredients, calories_per_serving, serving_size, restaurant_id, created_at, updated_at) 
SELECT 'Margherita Pizza', 'Classic pizza with tomato sauce, mozzarella, and basil', 250.00, 'Pizza', 'https://images.unsplash.com/photo-1604382354936-07c5d9983bd3', true, false, false, true, 15, 'Pizza Dough, Tomato Sauce, Mozzarella, Basil', 320, '1 pizza', (SELECT id FROM restaurants WHERE name = 'Pizza Corner'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Margherita Pizza' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Pizza Corner'));

INSERT INTO menu_items (name, description, price, category, image_url, is_vegetarian, is_vegan, is_spicy, is_available, preparation_time_minutes, ingredients, calories_per_serving, serving_size, restaurant_id, created_at, updated_at) 
SELECT 'Pepperoni Pizza', 'Spicy pepperoni with mozzarella cheese', 320.00, 'Pizza', 'https://images.unsplash.com/photo-1628840042765-356cda07504e', false, false, true, true, 15, 'Pizza Dough, Tomato Sauce, Mozzarella, Pepperoni', 420, '1 pizza', (SELECT id FROM restaurants WHERE name = 'Pizza Corner'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Pepperoni Pizza' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Pizza Corner'));

INSERT INTO menu_items (name, description, price, category, image_url, is_vegetarian, is_vegan, is_spicy, is_available, preparation_time_minutes, ingredients, calories_per_serving, serving_size, restaurant_id, created_at, updated_at) 
SELECT 'Pasta Carbonara', 'Creamy pasta with bacon and parmesan', 280.00, 'Pasta', 'https://images.unsplash.com/photo-1621996346565-e3dbc353d2e5', false, false, false, true, 12, 'Pasta, Cream, Bacon, Parmesan, Eggs', 480, '1 serving', (SELECT id FROM restaurants WHERE name = 'Pizza Corner'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Pasta Carbonara' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Pizza Corner'));

INSERT INTO menu_items (name, description, price, category, image_url, is_vegetarian, is_vegan, is_spicy, is_available, preparation_time_minutes, ingredients, calories_per_serving, serving_size, restaurant_id, created_at, updated_at) 
SELECT 'Caesar Salad', 'Fresh romaine lettuce with caesar dressing', 180.00, 'Salads', 'https://images.unsplash.com/photo-1546793665-c74683f339c1', true, false, false, true, 8, 'Romaine Lettuce, Croutons, Parmesan, Caesar Dressing', 220, '1 bowl', (SELECT id FROM restaurants WHERE name = 'Pizza Corner'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Caesar Salad' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Pizza Corner'));

INSERT INTO menu_items (name, description, price, category, image_url, is_vegetarian, is_vegan, is_spicy, is_available, preparation_time_minutes, ingredients, calories_per_serving, serving_size, restaurant_id, created_at, updated_at) 
SELECT 'Garlic Bread', 'Toasted bread with garlic butter', 120.00, 'Appetizers', 'https://images.unsplash.com/photo-1573140247632-f8fd74997d5c', true, false, false, true, 10, 'Bread, Garlic, Butter, Herbs', 280, '4 pieces', (SELECT id FROM restaurants WHERE name = 'Pizza Corner'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Garlic Bread' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Pizza Corner'));

-- Burger King Menu
INSERT INTO menu_items (name, description, price, category, image_url, is_vegetarian, is_vegan, is_spicy, is_available, preparation_time_minutes, ingredients, calories_per_serving, serving_size, restaurant_id, created_at, updated_at) 
SELECT 'Whopper', 'Flame-grilled beef patty with fresh vegetables', 220.00, 'Burgers', 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd', false, false, false, true, 8, 'Beef Patty, Bun, Lettuce, Tomato, Onion, Pickles', 650, '1 burger', (SELECT id FROM restaurants WHERE name = 'Burger King'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Whopper' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Burger King'));

INSERT INTO menu_items (name, description, price, category, image_url, is_vegetarian, is_vegan, is_spicy, is_available, preparation_time_minutes, ingredients, calories_per_serving, serving_size, restaurant_id, created_at, updated_at) 
SELECT 'Chicken Burger', 'Crispy chicken breast with mayo and lettuce', 180.00, 'Burgers', 'https://images.unsplash.com/photo-1606755962773-d324e9a13086', false, false, false, true, 8, 'Chicken Breast, Bun, Mayo, Lettuce', 520, '1 burger', (SELECT id FROM restaurants WHERE name = 'Burger King'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Chicken Burger' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Burger King'));

INSERT INTO menu_items (name, description, price, category, image_url, is_vegetarian, is_vegan, is_spicy, is_available, preparation_time_minutes, ingredients, calories_per_serving, serving_size, restaurant_id, created_at, updated_at) 
SELECT 'Veggie Burger', 'Plant-based patty with fresh vegetables', 160.00, 'Burgers', 'https://images.unsplash.com/photo-1525059696034-4967a729002e', true, true, false, true, 8, 'Veggie Patty, Bun, Lettuce, Tomato, Onion', 380, '1 burger', (SELECT id FROM restaurants WHERE name = 'Burger King'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Veggie Burger' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Burger King'));

INSERT INTO menu_items (name, description, price, category, image_url, is_vegetarian, is_vegan, is_spicy, is_available, preparation_time_minutes, ingredients, calories_per_serving, serving_size, restaurant_id, created_at, updated_at) 
SELECT 'French Fries', 'Crispy golden potato fries', 80.00, 'Sides', 'https://images.unsplash.com/photo-1576107232684-1279f390859f', true, true, false, true, 5, 'Potatoes, Salt', 320, '1 serving', (SELECT id FROM restaurants WHERE name = 'Burger King'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'French Fries' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Burger King'));

INSERT INTO menu_items (name, description, price, category, image_url, is_vegetarian, is_vegan, is_spicy, is_available, preparation_time_minutes, ingredients, calories_per_serving, serving_size, restaurant_id, created_at, updated_at) 
SELECT 'Coca Cola', 'Refreshing cola drink', 50.00, 'Beverages', 'https://images.unsplash.com/photo-1629203851122-3726ecdf080e', true, true, false, true, 2, 'Carbonated Water, Sugar, Cola Extract', 140, '1 can', (SELECT id FROM restaurants WHERE name = 'Burger King'), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Coca Cola' AND restaurant_id = (SELECT id FROM restaurants WHERE name = 'Burger King'));

-- Note: The BCrypt hash '$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa' represents 'password123'
-- This is a proper BCrypt hash generated for the password 'password123'