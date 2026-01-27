#!/bin/bash

# Food Delivery Platform - Microservices Stop Script
# This script stops all microservices and infrastructure

echo "🛑 Stopping Food Delivery Platform Microservices..."
echo "=================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_header() {
    echo -e "${BLUE}$1${NC}"
}

# Function to stop a service
stop_service() {
    local service_name=$1
    
    if [ -f "logs/${service_name}.pid" ]; then
        local pid=$(cat "logs/${service_name}.pid")
        if ps -p $pid > /dev/null 2>&1; then
            print_status "Stopping $service_name (PID: $pid)..."
            kill $pid
            sleep 2
            
            # Force kill if still running
            if ps -p $pid > /dev/null 2>&1; then
                print_status "Force stopping $service_name..."
                kill -9 $pid
            fi
        fi
        rm -f "logs/${service_name}.pid"
    else
        print_status "$service_name PID file not found, may already be stopped"
    fi
}

print_header "Step 1: Stopping Microservices"

# Stop services in reverse order
stop_service "API Gateway"
stop_service "Reporting Service"
stop_service "Delivery Service"
stop_service "Payment Service"
stop_service "Order Service"
stop_service "Cart Service"
stop_service "Restaurant Service"
stop_service "User Service"
stop_service "Auth Service"
stop_service "Config Server"
stop_service "Service Discovery"

print_header "Step 2: Stopping Infrastructure Services"

print_status "Stopping Docker containers..."
docker-compose down

# Optional: Remove volumes (uncomment if you want to clear all data)
# print_status "Removing Docker volumes..."
# docker-compose down -v

print_header "Step 3: Cleanup"

# Kill any remaining Java processes (be careful with this)
print_status "Checking for remaining Java processes..."
pkill -f "spring-boot:run" 2>/dev/null || true

print_status "Cleanup completed"

print_header "🛑 All Services Stopped Successfully!"
echo "=================================================="
print_status "All microservices have been stopped"
print_status "Docker containers have been stopped"
print_status "Log files are preserved in the 'logs' directory"
echo ""
print_status "To start services again, run: ./start-microservices.sh"
print_status "To clear all data, run: docker-compose down -v"