Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host "               VILLAGECONNECT - SYSTEM SUMMARY & ACTIVITY STATS               " -ForegroundColor Yellow
Write-Host "==============================================================================" -ForegroundColor Cyan

$token = (Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"email":"admin@villageconnect.com","password":"admin123"}').token
$stats = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/stats" -Headers @{Authorization="Bearer $token"}

$stats | Format-List
