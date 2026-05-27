#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR/.."

export HMS_DB_URL="${HMS_DB_URL:-jdbc:mysql://localhost:3307/hms_db}"
export HMS_DB_USER="${HMS_DB_USER:-root}"
export HMS_DB_PASSWORD="${HMS_DB_PASSWORD:-agrawalmm_3}"

echo "Compiling Hospital Management System..."
javac -cp ".:HospitalManagementSystem/lib/mysql-connector-j-8.0.33.jar" \
  HospitalManagementSystem/HMS.java \
  HospitalManagementSystem/AppointmentRow.java \
  HospitalManagementSystem/HMSApp.java

echo "Starting Hospital Management System..."
java -cp ".:HospitalManagementSystem/lib/mysql-connector-j-8.0.33.jar" HospitalManagementSystem.HMSApp
