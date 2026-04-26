-- Sample data for Menu Service
-- This file is loaded on application startup

-- Menu items for Spice Palace (Restaurant ID: 1)
INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 1, 'Butter Chicken', 'Creamy tomato-based curry with tender chicken pieces', 320.00, 'Main Course', 'https://example.com/butter-chicken.jpg', true, false, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 1 AND name = 'Butter Chicken');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 1, 'Paneer Tikka Masala', 'Grilled cottage cheese in rich tomato gravy', 280.00, 'Main Course', 'https://example.com/paneer-tikka-masala.jpg', true, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 1 AND name = 'Paneer Tikka Masala');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 1, 'Garlic Naan', 'Soft bread with garlic and herbs', 80.00, 'Breads', 'https://example.com/garlic-naan.jpg', true, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 1 AND name = 'Garlic Naan');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 1, 'Chicken Biryani', 'Aromatic basmati rice with spiced chicken', 380.00, 'Rice & Biryani', 'https://example.com/chicken-biryani.jpg', true, false, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 1 AND name = 'Chicken Biryani');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 1, 'Vegetable Biryani', 'Fragrant rice with mixed vegetables and spices', 320.00, 'Rice & Biryani', 'https://example.com/veg-biryani.jpg', true, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 1 AND name = 'Vegetable Biryani');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 1, 'Mango Lassi', 'Refreshing yogurt drink with mango', 120.00, 'Beverages', 'https://example.com/mango-lassi.jpg', true, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 1 AND name = 'Mango Lassi');

-- Menu items for Pizza Corner (Restaurant ID: 2)
INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 2, 'Margherita Pizza', 'Classic pizza with tomato sauce, mozzarella and basil', 250.00, 'Main Course', 'https://example.com/margherita-pizza.jpg', true, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 2 AND name = 'Margherita Pizza');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 2, 'Pepperoni Pizza', 'Pizza topped with pepperoni and cheese', 320.00, 'Main Course', 'https://example.com/pepperoni-pizza.jpg', true, false, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 2 AND name = 'Pepperoni Pizza');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 2, 'Chicken Alfredo Pasta', 'Creamy pasta with grilled chicken', 280.00, 'Main Course', 'https://example.com/chicken-alfredo.jpg', true, false, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 2 AND name = 'Chicken Alfredo Pasta');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 2, 'Caesar Salad', 'Fresh romaine lettuce with caesar dressing', 180.00, 'Salads', 'https://example.com/caesar-salad.jpg', true, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 2 AND name = 'Caesar Salad');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 2, 'Garlic Bread', 'Toasted bread with garlic butter', 120.00, 'Appetizers', 'https://example.com/garlic-bread.jpg', true, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 2 AND name = 'Garlic Bread');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 2, 'Tiramisu', 'Classic Italian dessert with coffee and mascarpone', 150.00, 'Desserts', 'https://example.com/tiramisu.jpg', true, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 2 AND name = 'Tiramisu');

-- Menu items for Burger King Express (Restaurant ID: 3)
INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 3, 'Whopper Burger', 'Flame-grilled beef patty with fresh vegetables', 220.00, 'Fast Food', 'https://example.com/whopper.jpg', true, false, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 3 AND name = 'Whopper Burger');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 3, 'Veggie Burger', 'Plant-based patty with fresh vegetables', 180.00, 'Fast Food', 'https://example.com/veggie-burger.jpg', true, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 3 AND name = 'Veggie Burger');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 3, 'Chicken Nuggets', 'Crispy chicken pieces with dipping sauce', 160.00, 'Fast Food', 'https://example.com/chicken-nuggets.jpg', true, false, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 3 AND name = 'Chicken Nuggets');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 3, 'French Fries', 'Golden crispy potato fries', 80.00, 'Snacks', 'https://example.com/french-fries.jpg', true, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 3 AND name = 'French Fries');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 3, 'Coca Cola', 'Refreshing cola drink', 60.00, 'Beverages', 'https://example.com/coca-cola.jpg', true, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 3 AND name = 'Coca Cola');

-- Menu items for Dragon Wok (Restaurant ID: 4)
INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 4, 'Kung Pao Chicken', 'Spicy stir-fried chicken with peanuts', 280.00, 'Chinese', 'https://example.com/kung-pao-chicken.jpg', true, false, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 4 AND name = 'Kung Pao Chicken');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 4, 'Vegetable Fried Rice', 'Wok-fried rice with mixed vegetables', 220.00, 'Chinese', 'https://example.com/veg-fried-rice.jpg', true, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 4 AND name = 'Vegetable Fried Rice');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 4, 'Sweet and Sour Pork', 'Crispy pork with sweet and sour sauce', 320.00, 'Chinese', 'https://example.com/sweet-sour-pork.jpg', true, false, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 4 AND name = 'Sweet and Sour Pork');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 4, 'Spring Rolls', 'Crispy vegetable spring rolls with dipping sauce', 150.00, 'Appetizers', 'https://example.com/spring-rolls.jpg', true, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 4 AND name = 'Spring Rolls');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 4, 'Hot and Sour Soup', 'Traditional Chinese soup with tofu and vegetables', 120.00, 'Soups', 'https://example.com/hot-sour-soup.jpg', true, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 4 AND name = 'Hot and Sour Soup');

-- Menu items for Taco Bell (Restaurant ID: 5)
INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 5, 'Chicken Quesadilla', 'Grilled tortilla with chicken and cheese', 200.00, 'Fast Food', 'https://example.com/chicken-quesadilla.jpg', true, false, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 5 AND name = 'Chicken Quesadilla');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 5, 'Veggie Burrito', 'Flour tortilla with beans, rice and vegetables', 180.00, 'Fast Food', 'https://example.com/veggie-burrito.jpg', true, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 5 AND name = 'Veggie Burrito');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 5, 'Beef Tacos', 'Soft tacos with seasoned beef and toppings', 160.00, 'Fast Food', 'https://example.com/beef-tacos.jpg', true, false, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 5 AND name = 'Beef Tacos');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 5, 'Nachos Supreme', 'Tortilla chips with cheese, beans and jalapeños', 140.00, 'Snacks', 'https://example.com/nachos-supreme.jpg', true, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 5 AND name = 'Nachos Supreme');

-- Some unavailable items for testing
INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 1, 'Fish Curry', 'Spicy fish curry with coconut milk', 350.00, 'Main Course', 'https://example.com/fish-curry.jpg', false, false, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 1 AND name = 'Fish Curry');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url, is_available, is_vegetarian, created_at, updated_at) 
SELECT 2, 'Seafood Pizza', 'Pizza with mixed seafood toppings', 420.00, 'Main Course', 'https://example.com/seafood-pizza.jpg', false, false, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE restaurant_id = 2 AND name = 'Seafood Pizza');