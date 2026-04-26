@echo off
echo Setting up PostgreSQL databases for Food Delivery Microservices...

REM Create the auth service database
psql -h localhost -p 5433 -U postgres -c "CREATE DATABASE auth_service_db;" 2>nul
if %errorlevel% equ 0 (
    echo ✓ auth_service_db created successfully
) else (
    echo ℹ auth_service_db already exists or connection failed
)

REM Create other service databases
psql -h localhost -p 5433 -U postgres -c "CREATE DATABASE restaurant_service_db;" 2>nul
psql -h localhost -p 5433 -U postgres -c "CREATE DATABASE menu_service_db;" 2>nul
psql -h localhost -p 5433 -U postgres -c "CREATE DATABASE order_service_db;" 2>nul
psql -h localhost -p 5433 -U postgres -c "CREATE DATABASE payment_service_db;" 2>nul
psql -h localhost -p 5433 -U postgres -c "CREATE DATABASE delivery_service_db;" 2>nul
psql -h localhost -p 5433 -U postgres -c "CREATE DATABASE notification_service_db;" 2>nul

echo.
echo PostgreSQL setup completed!
echo.
echo Database connection details:
echo - Host: localhost
echo - Port: 5433
echo - Username: postgres
echo - Password: password
echo.
echo Databases created:
echo - auth_service_db
echo - restaurant_service_db
echo - menu_service_db
echo - order_service_db
echo - payment_service_db
echo - delivery_service_db
echo - notification_service_db