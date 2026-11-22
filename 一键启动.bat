@echo off
chcp 65001 >nul
echo ============================================
echo   Homework Management System - Auto Start
echo ============================================
echo.

echo [1/2] Starting Spring Boot Backend...
cd /d "C:\学生作业管理系统\后端\homework-system"
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot"
set "PATH=%JAVA_HOME%\bin;C:\Users\28467\apache-maven-3.9.16\bin;%PATH%"
start "Backend-StudentHomework" cmd /k "mvn spring-boot:run"
echo Backend is starting on port 8090
echo.
echo   Please wait 30~60 seconds, then check the black
echo   window for "Started HomeworkApplication" message.
echo.

echo [2/2] Starting Nginx Frontend...
cd /d "C:\nginx\nginx-1.30.4"
nginx -s reload 2>nul
if errorlevel 1 (
    start "" nginx.exe
    echo Nginx started on port 80
) else (
    echo Nginx reloaded on port 80
)
echo.

echo ============================================
echo   System is Ready!
echo   URL: http://lidongna
echo.
echo   Test Accounts (password: 123456):
echo     Admin:    admin
echo     Teacher:  teacher01
echo     Student:  student001
echo ============================================
echo.
echo Press any key to open the browser...
pause >nul
start http://lidongna
