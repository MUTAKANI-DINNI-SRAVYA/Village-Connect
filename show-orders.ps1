Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host "               VILLAGECONNECT - LIVE COMMERCE & ORDERS IN DATABASE            " -ForegroundColor Yellow
Write-Host "==============================================================================" -ForegroundColor Cyan

$token = (Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"email":"admin@villageconnect.com","password":"admin123"}').token
$orders = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/orders" -Headers @{Authorization="Bearer $token"}

$orders | Format-Table orderId, productName, totalAmount, paymentMethod, status, customerName, deliveryAddress -AutoSize
$totalRevenue = ($orders | Measure-Object -Property totalAmount -Sum).Sum
Write-Host "Total Orders: $($orders.Count) | Total Gross Revenue: Rs. $totalRevenue" -ForegroundColor Green
