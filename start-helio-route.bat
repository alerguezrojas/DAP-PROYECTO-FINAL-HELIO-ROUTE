@echo off
echo ===================================================
echo   HelioRoute - Launcher
echo ===================================================
echo.

echo [1/3] Stopping previous Java processes...
taskkill /F /IM java.exe /FI "STATUS eq RUNNING" >nul 2>&1
echo Done.

echo.
echo [2/3] Starting Helio Calculation Service (Backend)...
cd helio-calculation-service
start "Helio Backend (8083)" /MIN mvn spring-boot:run
cd ..

echo.
echo [3/3] Starting Helio Web Interface (Frontend)...
cd helio-web-interface
start "Helio Frontend (8084)" /MIN mvn spring-boot:run
cd ..

echo.
echo ===================================================
echo   System starting up...
echo   Backend will be on: http://localhost:8083
echo   Frontend will be on: http://localhost:8084
echo.
echo   Please wait a few moments for Spring Boot to load.
echo ===================================================
pause
