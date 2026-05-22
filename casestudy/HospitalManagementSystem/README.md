# Hospital Management System - JavaFX Frontend

## 📋 Project Structure

This Hospital Management System has been converted to a JavaFX GUI application with the following components:

1. **HMSBackend.java** - Database backend with all business logic
2. **HMSApp.java** - JavaFX frontend application

## 🛠️ Prerequisites

- Java 17 or higher
- MySQL Server
- JavaFX SDK
- Maven (optional, for dependency management)

## 🗄️ Database Setup

### 1. Create Database and Tables

```sql
CREATE DATABASE hms_db;
USE hms_db;

CREATE TABLE patients (
    patientID VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    patientType VARCHAR(20),
    rateOrFee INT
);

CREATE TABLE doctors (
    docID VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100)
);

CREATE TABLE appointments (
    appointmentID INT AUTO_INCREMENT PRIMARY KEY,
    patientID VARCHAR(50),
    docID VARCHAR(50),
    appointmentDate DATE,
    FOREIGN KEY (patientID) REFERENCES patients(patientID),
    FOREIGN KEY (docID) REFERENCES doctors(docID)
);
```

### 2. Update Database Credentials

Edit `HMSBackend.java` and update:
```java
private static final String URL = "jdbc:mysql://localhost:3306/hms_db";
private static final String USER = "root";
private static final String PASSWORD = "your_password";
```

## 📦 Dependencies

### Required JAR Files

1. **MySQL JDBC Driver** (mysql-connector-java-8.x.x.jar)
   - Download from: https://dev.mysql.com/downloads/connector/j/

2. **JavaFX SDK**
   - Download from: https://gluonhq.com/products/javafx/

## 🚀 Compilation & Execution

### Option 1: Using Command Line

#### On Windows:

```powershell
# Set paths
$JAVAFX_HOME = "C:\path\to\javafx-sdk"
$MYSQL_JAR = "C:\path\to\mysql-connector-java-8.x.x.jar"

# Compile
javac --module-path $JAVAFX_HOME\lib --add-modules javafx.controls,javafx.fxml -cp $MYSQL_JAR HMSBackend.java HMSApp.java

# Run
java --module-path $JAVAFX_HOME\lib --add-modules javafx.controls,javafx.fxml -cp .;$MYSQL_JAR HMSApp
```

#### On Linux/Mac:

```bash
# Set paths
export JAVAFX_HOME=/path/to/javafx-sdk
export MYSQL_JAR=/path/to/mysql-connector-java-8.x.x.jar

# Compile
javac --module-path $JAVAFX_HOME/lib --add-modules javafx.controls,javafx.fxml -cp $MYSQL_JAR HMSBackend.java HMSApp.java

# Run
java --module-path $JAVAFX_HOME/lib --add-modules javafx.controls,javafx.fxml -cp .:$MYSQL_JAR HMSApp
```

### Option 2: Using Maven

Create a `pom.xml` in the HospitalManagementSystem directory:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.hospital</groupId>
    <artifactId>hms-javafx</artifactId>
    <version>1.0</version>

    <properties>
        <javafx.version>21.0.1</javafx.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>0.0.8</version>
                <configuration>
                    <mainClass>HMSApp</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

Then run:
```bash
mvn javafx:run
```

## 🎯 Features

### Main Menu
- Add Patient
- Add Doctor
- Schedule Appointment
- View Appointments
- Check Bed Status
- Exit

### Add Patient
- Register Insider (admitted to bed)
- Register Visitor (consultation only)
- Automatic bed capacity checking

### Add Doctor
- Register doctors with specialization

### Schedule Appointment
- Select patient and doctor
- Choose appointment date
- Automatic validation

### View Appointments
- Display all scheduled appointments
- Patient-Doctor mapping
- Appointment dates

### Bed Status
- Real-time bed occupancy
- Visual progress bar
- Available beds count

## 📝 Notes

- The system validates bed capacity when adding Insider patients
- All data is persisted in MySQL database
- The JavaFX UI provides a user-friendly interface for all operations
- Error handling with user-friendly messages

## ⚠️ Troubleshooting

### MySQL Connection Error
- Ensure MySQL server is running
- Verify credentials in HMSBackend.java
- Check database exists: `SHOW DATABASES;`

### JavaFX Module Error
- Ensure JavaFX SDK path is correct
- Add `--module-path` and `--add-modules` to compilation and execution

### Driver Not Found
- Download MySQL JDBC driver
- Add to classpath during compilation

## 📞 Support

If you encounter any issues, check:
1. Database connection and credentials
2. JavaFX SDK installation
3. MySQL JDBC driver in classpath
4. Java version compatibility (17+)
