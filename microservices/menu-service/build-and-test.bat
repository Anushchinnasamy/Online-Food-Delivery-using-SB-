@echo off
echo Building Menu Service...
cd /d "%~dp0"

echo.
echo === Cleaning and compiling ===
call mvn clean compile

if %ERRORLEVEL% neq 0 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo === Running tests ===
call mvn test

if %ERRORLEVEL% neq 0 (
    echo Tests failed!
    pause
    exit /b 1
)

echo.
echo === Packaging ===
call mvn package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo Packaging failed!
    pause
    exit /b 1
)

echo.
echo === Build completed successfully! ===
echo JAR file created: target/menu-service-1.0.0.jar
echo.
echo To run the service:
echo java -jar target/menu-service-1.0.0.jar
echo.
pause