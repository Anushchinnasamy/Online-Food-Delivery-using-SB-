# API Testing Script for Food Delivery Platform
# Run this after starting the application with: mvn spring-boot:run

$baseUrl = "http://localhost:8081/api"
$testResults = @()

Write-Host "🧪 Food Delivery Platform API Testing" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# Function to test an endpoint
function Test-Endpoint {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [object]$Body = $null,
        [hashtable]$Headers = @{},
        [int]$ExpectedStatus = 200
    )
    
    Write-Host "Testing: $Name" -ForegroundColor Yellow
    Write-Host "  $Method $Url" -ForegroundColor Gray
    
    try {
        $params = @{
            Uri = $Url
            Method = $Method
            Headers = $Headers
            ContentType = "application/json"
        }
        
        if ($Body) {
            $params.Body = ($Body | ConvertTo-Json -Depth 10)
        }
        
        $response = Invoke-RestMethod @params -ErrorAction Stop
        
        Write-Host "  ✅ SUCCESS - Status: 200" -ForegroundColor Green
        Write-Host "  Response: $($response | ConvertTo-Json -Depth 2 -Compress)" -ForegroundColor Gray
        Write-Host ""
        
        return @{
            Name = $Name
            Status = "PASS"
            Response = $response
        }
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        Write-Host "  ❌ FAILED - Status: $statusCode" -ForegroundColor Red
        Write-Host "  Error: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host ""
        
        return @{
            Name = $Name
            Status = "FAIL"
            Error = $_.Exception.Message
        }
    }
}

# Check if server is running
Write-Host "Checking if server is running..." -ForegroundColor Cyan
try {
    $null = Invoke-RestMethod -Uri "$baseUrl/restaurants" -Method GET -TimeoutSec 5
    Write-Host "✅ Server is running!" -ForegroundColor Green
    Write-Host ""
}
catch {
    Write-Host "❌ Server is not running!" -ForegroundColor Red
    Write-Host "Please start the server with: mvn spring-boot:run" -ForegroundColor Yellow
    Write-Host ""
    exit 1
}

# Test 1: Login as Customer
Write-Host "=== AUTHENTICATION TESTS ===" -ForegroundColor Cyan
$loginResult = Test-Endpoint `
    -Name "Login as Customer" `
    -Method "POST" `
    -Url "$baseUrl/auth/login" `
    -Body @{
        email = "john.doe@gmail.com"
        password = "password123"
    }

$customerToken = $loginResult.Response.accessToken
$testResults += $loginResult

# Test 2: Login as Restaurant Admin
$restaurantLoginResult = Test-Endpoint `
    -Name "Login as Restaurant Admin" `
    -Method "POST" `
    -Url "$baseUrl/auth/login" `
    -Body @{
        email = "raj@spicepalace.com"
        password = "password123"
    }

$restaurantToken = $restaurantLoginResult.Response.accessToken
$testResults += $restaurantLoginResult

# Test 3: Get Current User
if ($customerToken) {
    $testResults += Test-Endpoint `
        -Name "Get Current User" `
        -Method "GET" `
        -Url "$baseUrl/auth/me" `
        -Headers @{ Authorization = "Bearer $customerToken" }
}

Write-Host "=== RESTAURANT TESTS ===" -ForegroundColor Cyan

# Test 4: Get All Restaurants (Public)
$testResults += Test-Endpoint `
    -Name "Get All Restaurants" `
    -Method "GET" `
    -Url "$baseUrl/restaurants"

# Test 5: Get Restaurant by ID (Public)
$testResults += Test-Endpoint `
    -Name "Get Restaurant by ID" `
    -Method "GET" `
    -Url "$baseUrl/restaurants/1"

# Test 6: Search Restaurants by City (Public)
$testResults += Test-Endpoint `
    -Name "Search Restaurants by City" `
    -Method "GET" `
    -Url "$baseUrl/restaurants/search?city=Bangalore"

# Test 7: Search Restaurants by Cuisine (Public)
$testResults += Test-Endpoint `
    -Name "Search Restaurants by Cuisine" `
    -Method "GET" `
    -Url "$baseUrl/restaurants/search?cuisine=Indian"

# Test 8: Get My Restaurant (Restaurant Admin)
if ($restaurantToken) {
    $testResults += Test-Endpoint `
        -Name "Get My Restaurant" `
        -Method "GET" `
        -Url "$baseUrl/restaurants/my" `
        -Headers @{ Authorization = "Bearer $restaurantToken" }
}

Write-Host "=== MENU ITEM TESTS ===" -ForegroundColor Cyan

# Test 9: Get Menu Items for Restaurant (Public)
$testResults += Test-Endpoint `
    -Name "Get Menu Items for Restaurant" `
    -Method "GET" `
    -Url "$baseUrl/menu-items/restaurant/1"

# Test 10: Get Menu Item by ID (Public)
$testResults += Test-Endpoint `
    -Name "Get Menu Item by ID" `
    -Method "GET" `
    -Url "$baseUrl/menu-items/1"

# Test 11: Search Menu Items (Public)
$testResults += Test-Endpoint `
    -Name "Search Menu Items" `
    -Method "GET" `
    -Url "$baseUrl/menu-items/search?name=Butter"

# Test 12: Get Vegetarian Items (Public)
$testResults += Test-Endpoint `
    -Name "Get Vegetarian Items" `
    -Method "GET" `
    -Url "$baseUrl/menu-items/search?restaurantId=1&vegetarian=true"

Write-Host "=== ORDER TESTS ===" -ForegroundColor Cyan

# Test 13: Place Order (Customer)
if ($customerToken) {
    $orderResult = Test-Endpoint `
        -Name "Place Order" `
        -Method "POST" `
        -Url "$baseUrl/orders" `
        -Headers @{ Authorization = "Bearer $customerToken" } `
        -Body @{
            restaurantId = 1
            items = @(
                @{
                    menuItemId = 1
                    quantity = 2
                    specialInstructions = "Extra spicy"
                },
                @{
                    menuItemId = 4
                    quantity = 1
                }
            )
            deliveryAddress = "123 Main Street, HSR Layout, Bangalore"
            deliveryLatitude = 12.9082
            deliveryLongitude = 77.6476
            customerPhone = "9876543216"
            specialInstructions = "Ring doorbell twice"
            paymentMethod = "CREDIT_CARD"
        }
    
    $orderId = $orderResult.Response.id
    $testResults += $orderResult
    
    # Test 14: Get My Orders (Customer)
    $testResults += Test-Endpoint `
        -Name "Get My Orders" `
        -Method "GET" `
        -Url "$baseUrl/orders/my" `
        -Headers @{ Authorization = "Bearer $customerToken" }
    
    # Test 15: Get Order by ID (Customer)
    if ($orderId) {
        $testResults += Test-Endpoint `
            -Name "Get Order by ID" `
            -Method "GET" `
            -Url "$baseUrl/orders/$orderId" `
            -Headers @{ Authorization = "Bearer $customerToken" }
    }
}

# Test 16: Get Restaurant Orders (Restaurant Admin)
if ($restaurantToken) {
    $testResults += Test-Endpoint `
        -Name "Get Restaurant Orders" `
        -Method "GET" `
        -Url "$baseUrl/orders/restaurant/1" `
        -Headers @{ Authorization = "Bearer $restaurantToken" }
    
    # Test 17: Update Order Status (Restaurant Admin)
    if ($orderId) {
        $testResults += Test-Endpoint `
            -Name "Update Order Status to ACCEPTED" `
            -Method "PUT" `
            -Url "$baseUrl/orders/$orderId/status" `
            -Headers @{ Authorization = "Bearer $restaurantToken" } `
            -Body @{ status = "ACCEPTED" }
        
        $testResults += Test-Endpoint `
            -Name "Update Order Status to PREPARING" `
            -Method "PUT" `
            -Url "$baseUrl/orders/$orderId/status" `
            -Headers @{ Authorization = "Bearer $restaurantToken" } `
            -Body @{ status = "PREPARING" }
    }
}

# Test 18: Register New User
$testResults += Test-Endpoint `
    -Name "Register New User" `
    -Method "POST" `
    -Url "$baseUrl/auth/register" `
    -Body @{
        name = "Test User"
        email = "testuser$(Get-Random)@example.com"
        phone = "99999$(Get-Random -Minimum 10000 -Maximum 99999)"
        password = "password123"
        role = "CUSTOMER"
        address = "Test Address"
        city = "Bangalore"
        pincode = "560001"
    }

# Summary
Write-Host ""
Write-Host "=== TEST SUMMARY ===" -ForegroundColor Cyan
Write-Host "===================" -ForegroundColor Cyan
Write-Host ""

$passed = ($testResults | Where-Object { $_.Status -eq "PASS" }).Count
$failed = ($testResults | Where-Object { $_.Status -eq "FAIL" }).Count
$total = $testResults.Count

Write-Host "Total Tests: $total" -ForegroundColor White
Write-Host "Passed: $passed" -ForegroundColor Green
Write-Host "Failed: $failed" -ForegroundColor Red
Write-Host ""

if ($failed -eq 0) {
    Write-Host "🎉 ALL TESTS PASSED!" -ForegroundColor Green
} else {
    Write-Host "❌ Some tests failed. Check the output above for details." -ForegroundColor Red
    Write-Host ""
    Write-Host "Failed Tests:" -ForegroundColor Yellow
    $testResults | Where-Object { $_.Status -eq "FAIL" } | ForEach-Object {
        Write-Host "  - $($_.Name)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "Test completed at: $(Get-Date)" -ForegroundColor Gray
