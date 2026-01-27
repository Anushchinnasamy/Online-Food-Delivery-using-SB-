@echo off
echo.
echo ========================================
echo   Food Delivery Platform - Startup
echo ========================================
echo.

echo Starting PostgreSQL database...
echo Please ensure PostgreSQL is running on localhost:5432
echo Default database: fooddelivery
echo Default user: postgres
echo Default password: password
echo.

echo Starting Spring Boot backend...
cd backend
start "Backend Server" cmd /k "mvnw.cmd spring-boot:run"
cd ..

echo.
echo Waiting for backend to start...
timeout /t 10 /nobreak > nul

echo.
echo Starting frontend server...
cd frontend
start "Frontend Server" cmd /k "python -m http.server 8080"
cd ..

echo.
echo ========================================
echo   Application Started Successfully!
echo ========================================
echo.
echo Backend API: http://localhost:8080/api
echo Frontend:    http://localhost:8080
echo.
echo Demo Accounts (Password: password123):
echo - Customer:    john.doe@gmail.com
echo - Restaurant:  raj@spicepalace.com
echo - Delivery:    ravi.delivery@gmail.com
echo - Admin:       admin@fooddelivery.com
echo.
echo Press any key to open the application...
pause > nul

start http://localhost:8080

echo.
echo Application is now running!
echo Press Ctrl+C in the server windows to stop.
echo.
pause