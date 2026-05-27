@echo off
setlocal

cd /d "%~dp0\.."

if "%HMS_DB_URL%"=="" set "HMS_DB_URL=jdbc:mysql://localhost:3307/hms_db"
if "%HMS_DB_USER%"=="" set "HMS_DB_USER=root"
if "%HMS_DB_PASSWORD%"=="" set "HMS_DB_PASSWORD=agrawalmm_3"

echo Compiling Hospital Management System...
javac -cp ".;HospitalManagementSystem\lib\mysql-connector-j-8.0.33.jar" HospitalManagementSystem\HMS.java HospitalManagementSystem\AppointmentRow.java HospitalManagementSystem\HMSApp.java
if %ERRORLEVEL% neq 0 (
    echo Compilation failed.
    pause
    exit /b 1
)

echo Starting Hospital Management System...
java -cp ".;HospitalManagementSystem\lib\mysql-connector-j-8.0.33.jar" HospitalManagementSystem.HMSApp

endlocal
