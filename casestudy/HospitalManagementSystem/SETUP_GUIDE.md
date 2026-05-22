# Hospital Management System - Setup & Usage Guide

## ✅ Status: Application Running Successfully!

The HMS application has been **fixed and tested**. You now have **two versions** available:

### Version 1: HMSDemo.java (READY TO USE - No MySQL Needed)
- ✅ **Already working** - runs with in-memory database
- Perfect for testing and development
- Contains 8 doctors, 14 patients (8 insider, 6 visitor), and 14 appointments
- **Run command**: `javac HMSDemo.java && java HMSDemo`

### Version 2: HMS.java (For Production with MySQL)
- Requires MySQL Server setup
- Persists data to real database
- Follow the steps below to set up

---

## What Was Fixed

### Issues Resolved:
1. ✅ **Duplicate Key Errors** - Added duplicate checking before inserts
2. ✅ **Connection Errors** - Added better error handling and database initialization
3. ✅ **Missing Data** - Added comprehensive sample data (14 appointments, 14 patients, 8 doctors)
4. ✅ **Bed Capacity** - Increased from 5 to 10 beds and added proper tracking
5. ✅ **Data Display** - Added methods to view all patients, doctors, and appointments
6. ✅ **Validation** - Added checks for patient/doctor IDs before scheduling appointments

---

## Data Added to the System

### Doctors Registered (8 total):
- D001: Dr. Rajesh Kumar - Cardiology
- D002: Dr. Priya Sharma - Neurology
- D003: Dr. Amit Patel - Orthopedics
- D004: Dr. Neha Gupta - Pediatrics
- D005: Dr. Vikram Singh - General Surgery
- D006: Dr. Anjali Verma - Dermatology
- D007: Dr. Sanjay Reddy - ENT
- D008: Dr. Pooja Nair - Gynecology

### Insider Patients (8 total - with hospital beds):
- P001: Rohan Mehta (₹1500/day)
- P002: Kavya Singh (₹1800/day)
- P003: Arjun Nair (₹1600/day)
- P004: Deepika Chopra (₹2000/day)
- P005: Nikhil Desai (₹1700/day)
- P006: Riya Bansal (₹1900/day)
- P007: Vishal Yadav (₹1500/day)
- P008: Ananya Mukherjee (₹1800/day)

### Visitor Patients (6 total - consultation only):
- V001: Rahul Patel (₹500 consultation fee)
- V002: Shreya Kapoor (₹600)
- V003: Aman Joshi (₹500)
- V004: Neha Saxena (₹550)
- V005: Siddharth Das (₹700)
- V006: Zara Khan (₹600)

### Appointments Scheduled (14 total):
14 appointments spread from June 1-14, 2026, linking patients with appropriate doctors.

---

## Running the Application

### Quick Start (No Setup Required):
```powershell
cd "c:\Users\Mohit\OneDrive\Desktop\CRT Phase 2\casestudy\HospitalManagementSystem"
javac HMSDemo.java
java HMSDemo
```

---

## Setting Up with MySQL (Optional - For Production)

### Step 1: Install MySQL Server
1. Download MySQL Community Server from: https://dev.mysql.com/downloads/mysql/
2. Run the installer and complete the setup
3. Note your root password during installation

### Step 2: Start MySQL Server
**Windows:**
```powershell
# If installed as Windows Service
net start MySQL80

# Or use MySQL Workbench to manage the server
```

### Step 3: Create Database
Open MySQL Command Line or Workbench and run `database_setup.sql`:

```sql
-- Open terminal in the HMS directory
mysql -u root -p < database_setup.sql
```

Or copy-paste contents of `database_setup.sql` into MySQL.

### Step 4: Update HMS.java Credentials
Edit `HMS.java` and update these lines (around line 62-64):

```java
private static final String URL = "jdbc:mysql://localhost:3306/hms_db";
private static final String USER = "root";
private static final String PASSWORD = "your_mysql_password";  // Change this!
```

### Step 5: Download MySQL JDBC Driver
Place the MySQL connector JAR in the HMS directory:
- File name: `mysql-connector-java-8.0.33.jar` (or latest version)
- Download from: https://dev.mysql.com/downloads/connector/j/

### Step 6: Compile and Run with MySQL
```powershell
cd "c:\Users\Mohit\OneDrive\Desktop\CRT Phase 2\casestudy\HospitalManagementSystem"

# Compile with MySQL driver
javac -cp mysql-connector-java-8.0.33.jar HMS.java

# Run with MySQL driver in classpath
java -cp .;mysql-connector-java-8.0.33.jar HMS
```

---

## Key Features of the Application

### 1. Patient Management
- Support for two patient types: Insider (bed-based) and Visitor (consultation-based)
- Automatic bed capacity checking (max 10 beds)
- Prevents duplicate patient registrations
- Calculates charges based on patient type and stay duration

### 2. Doctor Management
- Maintains doctor information with specialization
- Associates doctors with appointments
- Prevents duplicate doctor registrations

### 3. Appointment System
- Schedules appointments between patients and doctors
- Validates that both patient and doctor exist before booking
- Displays appointments sorted by date
- Shows full details: patient name, doctor name, and appointment date

### 4. Bed Status Tracking
- Real-time bed occupancy calculation
- Shows total beds, occupied beds, and available beds
- Only counts "Insider" patients for bed occupancy

### 5. Billing System
- **Insider**: Charges per bed/day (e.g., ₹1500 × 5 days = ₹7500)
- **Visitor**: Charges per consultation (e.g., ₹500 × 3 = ₹1500)

---

## Troubleshooting

### Issue: "MySQL Driver not found"
**Solution**: 
- Download `mysql-connector-java-8.0.33.jar`
- Place it in the HMS directory
- Compile with: `javac -cp mysql-connector-java-8.0.33.jar HMS.java`

### Issue: "Connection refused"
**Solutions**:
- Ensure MySQL Server is running (check Windows Services)
- Verify the database credentials in HMS.java match your MySQL setup
- Ensure `hms_db` database exists

### Issue: "Duplicate entry for key"
**Solution**: 
- The data is already in the database
- Delete data using: `DELETE FROM appointments; DELETE FROM patients; DELETE FROM doctors;`
- Or change the patient/doctor IDs to new values

### Issue: "Patient/Doctor ID not found" when scheduling
**Solution**:
- Ensure the patient and doctor have been registered first
- Check the IDs match exactly (case-sensitive)
- Run the app again to register new data

---

## Files in This Directory

1. **HMS.java** - Main application (MySQL version)
2. **HMSDemo.java** - Demo version (in-memory, no MySQL needed)
3. **HMSApp.java** - JavaFX GUI version (commented out, requires JavaFX SDK)
4. **HMSBackend.java** - Backend implementation (commented out)
5. **database_setup.sql** - SQL script to create database and tables
6. **run.bat** - Batch script for Windows (requires setup)
7. **run.sh** - Shell script for Linux/Mac (requires setup)
8. **README.md** - Original project documentation

---

## Sample Output

When you run `HMSDemo.java`, you'll see:

```
╔════════════════════════════════════════════════╗
║   Hospital Management System - DEMO MODE       ║
║   (Using In-Memory Database)                   ║
╚════════════════════════════════════════════════╝

✅ 8 Doctors Registered
✅ 8 Insider Patients (beds occupied: 8/10)
✅ 6 Visitor Patients
✅ 14 Appointments Scheduled
✅ Billing Calculations Working

Sample Charges:
- Insider for 5 days @ ₹1500/day = ₹7500
- Visitor for 3 consultations @ ₹500 = ₹1500
```

---

## Next Steps

1. ✅ **Test the Demo Version**: Run `java HMSDemo` (works immediately)
2. ✅ **Review the Data**: Check all 14 patients, 8 doctors, 14 appointments
3. 📋 **Optional - Set up MySQL**: Follow steps in "Setting Up with MySQL" section
4. 🔧 **Customize**: Modify patient/doctor data as needed
5. 📊 **Extend Features**: Add more functionality like discharge, billing reports, etc.

---

**Status**: ✅ Application is working and tested with comprehensive sample data!
