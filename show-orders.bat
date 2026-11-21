@echo off
echo ==============================================================================
echo                 VILLAGECONNECT - LIVE ORDERS DATABASE TABLE
echo ==============================================================================
powershell -NoProfile -Command "$token = (Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method Post -ContentType 'application/json' -Body '{\"email\":\"admin@villageconnect.com\",\"password\":\"admin123\"}').token; $orders = Invoke-RestMethod -Uri 'http://localhost:8080/api/admin/orders' -Headers @{Authorization=\"Bearer $token\"}; $orders | Format-Table orderId, productName, totalAmount, paymentMethod, status, customerName, deliveryAddress -AutoSize"
pause
