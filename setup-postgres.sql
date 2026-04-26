-- Setup script for Food Delivery Microservices PostgreSQL databases

-- Create databases for each microservice
CREATE DATABASE IF NOT EXISTS auth_service_db;
CREATE DATABASE IF NOT EXISTS restaurant_service_db;
CREATE DATABASE IF NOT EXISTS menu_service_db;
CREATE DATABASE IF NOT EXISTS order_service_db;
CREATE DATABASE IF NOT EXISTS payment_service_db;
CREATE DATABASE IF NOT EXISTS delivery_service_db;
CREATE DATABASE IF NOT EXISTS notification_service_db;

-- Create a dedicated user for the microservices (optional)
-- CREATE USER fooddelivery_user WITH PASSWORD 'fooddelivery_pass';
-- GRANT ALL PRIVILEGES ON DATABASE auth_service_db TO fooddelivery_user;
-- GRANT ALL PRIVILEGES ON DATABASE restaurant_service_db TO fooddelivery_user;
-- GRANT ALL PRIVILEGES ON DATABASE menu_service_db TO fooddelivery_user;
-- GRANT ALL PRIVILEGES ON DATABASE order_service_db TO fooddelivery_user;
-- GRANT ALL PRIVILEGES ON DATABASE payment_service_db TO fooddelivery_user;
-- GRANT ALL PRIVILEGES ON DATABASE delivery_service_db TO fooddelivery_user;
-- GRANT ALL PRIVILEGES ON DATABASE notification_service_db TO fooddelivery_user;