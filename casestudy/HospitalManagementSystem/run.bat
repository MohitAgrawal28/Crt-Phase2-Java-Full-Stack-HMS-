@echo off
setlocal

cd /d "%~dp0\.."

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
