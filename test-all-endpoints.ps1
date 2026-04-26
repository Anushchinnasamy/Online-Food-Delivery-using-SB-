# Comprehensive API Testing Script
# Tests all endpoints and generates a report

$baseUrl = "http://localhost:8081/api"
$results = @()

function Test-Endpoint {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [object]$Body = $null,
        [hashtable]$Headers = @{}
    )
    
    try {
        $params = @{
            Uri = "$baseUrl$Url"
            Method = $Method
            Headers = $Headers
            ContentType = "application/json"
        }
        
        if ($Body) {
            $params.Body = ($Body | ConvertTo-Json -Depth 10)
        }
        
        $response = Invoke-RestMethod @params -ErrorAction Stop
        
        return @{
            Name = $Name
            Status = "✅ PASS"
            StatusCode = 200
            Response = $response
        }
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        return @{
            Name = $Name
            Status = "❌ FAIL"
            StatusCode = $statusCode
            Error = $_.Exception.Message
        }
    }
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "🧪 API ENDPOINT TESTING REPORT" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# 1. AUTHENTICATION TESTS
Write-Host "📝 1. AUTHENTICATION ENDPOINTS" -ForegroundColor Yellow
Write-Host "--------------------------------`n"

$loginResult = Test-Endpoint -Name "Login (Customer)" -Method POST -Url "/auth/login" -Body @{
    email = "john.doe@gmail.com"
    password = "password123"
}
$results += $loginResult
Write-Host "$($loginResult.Status) $($loginResult.Name)"

if ($loginResult.Status -eq "✅ PASS") {
    $customerToken = $loginResult.Response.accessToken
    $customerHeaders = @{ "Authorization" = "Bearer $customerToken" }
    Write-Host "   Token: $($customerToken.Substring(0, 20))..." -ForegroundColor Green
}

$loginRestaurantResult = Test-Endpoint -Name "Login (Restaurant Admin)" -Method POST -Url "/auth/login" -Body @{
    email = "raj@spicepalace.com"
    password = "password123"
}
$results += $loginRestaurantResult
Write-Host "$($loginRestaurantResult.Status) $($loginRestaurantResult.Name)"

if ($loginRestaurantResult.Status -eq "✅ PASS") {
    $restaurantToken = $loginRestaurantResult.Response.accessToken
    $restaurantHeaders = @{ "Authorization" = "Bearer $restaurantToken" }
}

$loginAdminResult = Test-Endpoint -Name "Login (Platform Admin)" -Method POST -Url "/auth/login" -Body @{
    email = "admin@fooddelivery.com"
    password = "password123"
}
$results += $loginAdminResult
Write-Host "$($loginAdminResult.Status) $($loginAdminResult.Name)"

if ($loginAdminResult.Status -eq "✅ PASS") {
    $adminToken = $loginAdminResult.Response.accessToken
    $adminHeaders = @{ "Authorization" = "Bearer $adminToken" }
}

# 2. RESTAURANT TESTS
Write-Host "`n📝 2. RESTAURANT ENDPOINTS" -ForegroundColor Yellow
Write-Host "--------------------------------`n"

$result = Test-Endpoint -Name "Get All Restaurants" -Method GET -Url "/restaurants"
$results += $result
Write-Host "$($result.Status) $($result.Name)"
if ($result.Status -eq "✅ PASS") {
    Write-Host "   Found $($result.Response.Count) restaurants" -ForegroundColor Green
    $restaurantId = $result.Response[0].id
}

$result = Test-Endpoint -Name "Get Restaurant by ID" -Method GET -Url "/restaurants/$restaurantId"
$results += $result
Write-Host "$($result.Status) $($result.Name)"

$result = Test-Endpoint -Name "Search Restaurants by City" -Method GET -Url "/restaurants/search?city=Bangalore"
$results += $result
Write-Host "$($result.Status) $($result.Name)"

$result = Test-Endpoint -Name "Search Restaurants by Cuisine" -Method GET -Url "/restaurants/search?cuisine=Indian"
$results += $result
Write-Host "$($result.Status) $($result.Name)"

if ($restaurantHeaders) {
    $result = Test-Endpoint -Name "Get My Restaurant" -Method GET -Url "/restaurants/my" -Headers $restaurantHeaders
    $results += $result
    Write-Host "$($result.Status) $($result.Name)"
}

# 3. MENU ITEM TESTS
Write-Host "`n📝 3. MENU ITEM ENDPOINTS" -ForegroundColor Yellow
Write-Host "--------------------------------`n"

$result = Test-Endpoint -Name "Get Menu Items for Restaurant" -Method GET -Url "/menu-items/restaurant/$restaurantId"
$results += $result
Write-Host "$($result.Status) $($result.Name)"
if ($result.Status -eq "✅ PASS") {
    Write-Host "   Found $($result.Response.Count) menu items" -ForegroundColor Green
    $menuItemId = $result.Response[0].id
}

$result = Test-Endpoint -Name "Get Menu Item by ID" -Method GET -Url "/menu-items/$menuItemId"
$results += $result
Write-Host "$($result.Status) $($result.Name)"

$result = Test-Endpoint -Name "Search Menu Items" -Method GET -Url "/menu-items/search?name=Butter"
$results += $result
Write-Host "$($result.Status) $($result.Name)"

$result = Test-Endpoint -Name "Get Vegetarian Items" -Method GET -Url "/menu-items/search?restaurantId=$restaurantId`&vegetarian=true"
$results += $result
Write-Host "$($result.Status) $($result.Name)"

# 4. ORDER TESTS
Write-Host "`n📝 4. ORDER ENDPOINTS" -ForegroundColor Yellow
Write-Host "--------------------------------`n"

if ($customerHeaders) {
    $orderResult = Test-Endpoint -Name "Place Order" -Method POST -Url "/orders" -Headers $customerHeaders -Body @{
        restaurantId = $restaurantId
        items = @(
            @{
                menuItemId = $menuItemId
                quantity = 2
                specialInstructions = "Extra spicy"
            }
        )
        deliveryAddress = "123 Main Street, HSR Layout, Bangalore"
        deliveryLatitude = 12.9082
        deliveryLongitude = 77.6476
        customerPhone = "9876543216"
        specialInstructions = "Ring the doorbell twice"
        paymentMethod = "CREDIT_CARD"
    }
    $results += $orderResult
    Write-Host "$($orderResult.Status) $($orderResult.Name)"
    
    if ($orderResult.Status -eq "✅ PASS") {
        $orderId = $orderResult.Response.id
        Write-Host "   Order ID: $orderId" -ForegroundColor Green
        Write-Host "   Order Number: $($orderResult.Response.orderNumber)" -ForegroundColor Green
        Write-Host "   Total Amount: ₹$($orderResult.Response.totalAmount)" -ForegroundColor Green
    }
    
    $result = Test-Endpoint -Name "Get My Orders" -Method GET -Url "/orders/my" -Headers $customerHeaders
    $results += $result
    Write-Host "$($result.Status) $($result.Name)"
    
    if ($orderId) {
        $result = Test-Endpoint -Name "Get Order by ID" -Method GET -Url "/orders/$orderId" -Headers $customerHeaders
        $results += $result
        Write-Host "$($result.Status) $($result.Name)"
    }
}

if ($restaurantHeaders -and $orderId) {
    $result = Test-Endpoint -Name "Get Restaurant Orders" -Method GET -Url "/orders/restaurant/$restaurantId" -Headers $restaurantHeaders
    $results += $result
    Write-Host "$($result.Status) $($result.Name)"
    
    $result = Test-Endpoint -Name "Update Order Status" -Method PUT -Url "/orders/$orderId/status" -Headers $restaurantHeaders -Body @{
        status = "ACCEPTED"
    }
    $results += $result
    Write-Host "$($result.Status) $($result.Name)"
}

if ($adminHeaders) {
    $result = Test-Endpoint -Name "Get Orders by Status" -Method GET -Url "/orders/status/PLACED" -Headers $adminHeaders
    $results += $result
    Write-Host "$($result.Status) $($result.Name)"
}

# SUMMARY
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "📊 TEST SUMMARY" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$passed = ($results | Where-Object { $_.Status -eq "✅ PASS" }).Count
$failed = ($results | Where-Object { $_.Status -eq "❌ FAIL" }).Count
$total = $results.Count

Write-Host "Total Tests: $total" -ForegroundColor White
Write-Host "Passed: $passed" -ForegroundColor Green
Write-Host "Failed: $failed" -ForegroundColor Red
Write-Host "Success Rate: $([math]::Round(($passed/$total)*100, 2))%`n" -ForegroundColor Cyan

if ($failed -gt 0) {
    Write-Host "Failed Tests:" -ForegroundColor Red
    $results | Where-Object { $_.Status -eq "❌ FAIL" } | ForEach-Object {
        Write-Host "  - $($_.Name) (Status: $($_.StatusCode))" -ForegroundColor Red
        if ($_.Error) {
            Write-Host "    Error: $($_.Error)" -ForegroundColor DarkRed
        }
    }
}

Write-Host "" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
