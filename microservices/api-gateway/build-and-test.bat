@echo off
echo ========================================
echo Building and Testing API Gateway
echo ========================================

echo.
echo [1/4] Cleaning previous builds...
call mvn clean

echo.
echo [2/4] Compiling source code...
call mvn compile

echo.
echo [3/4] Running tests...
call mvn test

echo.
echo [4/4] Packaging application...
call mvn package -DskipTests

echo.
echo ========================================
echo Build completed successfully!
echo ========================================
echo.
echo To run the application:
echo java -jar target/api-gateway-1.0.0.jar
echo.
echo Or use Maven:
echo mvn spring-boot:run
echo.
echo API Gateway will be available at: http://localhost:8080
echo Health check: http://localhost:8080/health
echo Gateway routes: http://localhost:8080/actuator/gateway/routes
echo ========================================

pause