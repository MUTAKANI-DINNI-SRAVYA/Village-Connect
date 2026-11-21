@echo off
echo ==============================================================================
echo                 VILLAGECONNECT - LIVE REGISTERED USERS IN DATABASE
echo ==============================================================================
powershell -NoProfile -Command "$token = (Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method Post -ContentType 'application/json' -Body '{\"email\":\"admin@villageconnect.com\",\"password\":\"admin123\"}').token; $users = Invoke-RestMethod -Uri 'http://localhost:8080/api/admin/users' -Headers @{Authorization=\"Bearer $token\"}; $users | Format-Table userId, name, email, phone, role, village -AutoSize"
pause
