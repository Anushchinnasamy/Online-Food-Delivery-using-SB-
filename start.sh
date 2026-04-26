#!/bin/bash

echo ""
echo "========================================"
echo "   Food Delivery Platform - Startup"
echo "========================================"
echo ""

echo "Starting PostgreSQL database..."
echo "Please ensure PostgreSQL is running on localhost:5432"
echo "Default database: fooddelivery"
echo "Default user: postgres"
echo "Default password: password"
echo ""

echo "Starting Spring Boot backend..."
cd backend
gnome-terminal --title="Backend Server" -- bash -c "./mvnw spring-boot:run; exec bash" 2>/dev/null || \
xterm -title "Backend Server" -e "./mvnw spring-boot:run" 2>/dev/null || \
osascript -e 'tell app "Terminal" to do script "cd '$(pwd)' && ./mvnw spring-boot:run"' 2>/dev/null || \
echo "Please run './mvnw spring-boot:run' in the backend directory manually"
cd ..

echo ""
echo "Waiting for backend to start..."
sleep 10

echo ""
echo "Starting frontend server..."
cd frontend
gnome-terminal --title="Frontend Server" -- bash -c "python3 -m http.server 8080; exec bash" 2>/dev/null || \
xterm -title "Frontend Server" -e "python3 -m http.server 8080" 2>/dev/null || \
osascript -e 'tell app "Terminal" to do script "cd '$(pwd)' && python3 -m http.server 8080"' 2>/dev/null || \
echo "Please run 'python3 -m http.server 8080' in the frontend directory manually"
cd ..

echo ""
echo "========================================"
echo "   Application Started Successfully!"
echo "========================================"
echo ""
echo "Backend API: http://localhost:8080/api"
echo "Frontend:    http://localhost:8080"
echo ""
echo "Demo Accounts (Password: password123):"
echo "- Customer:    john.doe@gmail.com"
echo "- Restaurant:  raj@spicepalace.com"
echo "- Delivery:    ravi.delivery@gmail.com"
echo "- Admin:       admin@fooddelivery.com"
echo ""

# Try to open browser
if command -v xdg-open > /dev/null; then
    xdg-open http://localhost:8080
elif command -v open > /dev/null; then
    open http://localhost:8080
elif command -v start > /dev/null; then
    start http://localhost:8080
else
    echo "Please open http://localhost:8080 in your browser"
fi

echo ""
echo "Application is now running!"
echo "Press Ctrl+C in the server terminals to stop."
echo ""