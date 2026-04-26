@echo off
echo ========================================
echo Building and Testing Payment Service
echo ========================================

echo.
echo [1/4] Cleaning previous builds...
call mvn clean
if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven clean failed
    exit /b 1
)

echo.
echo [2/4] Compiling source code...
call mvn compile
if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven compile failed
    exit /b 1
)

echo.
echo [3/4] Running tests...
call mvn test
if %ERRORLEVEL% neq 0 (
    echo ERROR: Tests failed
    exit /b 1
)

echo.
echo [4/4] Packaging application...
call mvn package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven package failed
    exit /b 1
)

echo.
echo ========================================
echo Payment Service Build Completed Successfully!
echo ========================================
echo.
echo JAR Location: target\payment-service-1.0.0.jar
echo.
echo To run the service:
echo java -jar target\payment-service-1.0.0.jar
echo.
echo Or use Maven:
echo mvn spring-boot:run
echo.
echo Service will be available at: http://localhost:8085
echo Health check: http://localhost:8085/payments/health
echo ========================================

pause