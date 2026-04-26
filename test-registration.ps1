$uri = "http://localhost:8081/auth/register"
$headers = @{
    "Content-Type" = "application/json"
}
$body = @{
    name = "Test User"
    email = "testuser$(Get-Random)@example.com"
    phone = "9876543210"
    password = "password123"
    role = "CUSTOMER"
} | ConvertTo-Json

Write-Host "Testing registration with:"
Write-Host $body

try {
    $response = Invoke-WebRequest -Uri $uri -Method POST -Body $body -Headers $headers -UseBasicParsing
    Write-Host "Success! Status: $($response.StatusCode)"
    Write-Host "Response: $($response.Content)"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body: $responseBody"
    }
}