@echo off
:: Check for administrative permissions
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo ========================================================
    echo ERROR: Administrator permissions required!
    echo Please right-click this file and select:
    echo   "Run as administrator"
    echo ========================================================
    pause
    exit /b 1
)

echo ========================================================
echo Step 1: Stopping MySQL80 service...
echo ========================================================
net stop MySQL80

echo ========================================================
echo Step 2: Applying password reset (setting password to: root)...
echo ========================================================
set "SQL_FILE=%~dp0reset-password.sql"
set "SQL_FILE_FORWARD=%SQL_FILE:\=/%"

start "" "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe" --defaults-file="C:\ProgramData\MySQL\MySQL Server 8.0\my.ini" --init-file="%SQL_FILE_FORWARD%" --console

echo Waiting for MySQL to apply password reset...
timeout /t 6 /nobreak >nul

echo Stopping temporary MySQL process...
taskkill /f /im mysqld.exe >nul 2>&1

echo ========================================================
echo Step 3: Starting MySQL80 service normally...
echo ========================================================
net start MySQL80

echo ========================================================
echo SUCCESS: Your MySQL root password has been reset to: root
echo ========================================================
pause
