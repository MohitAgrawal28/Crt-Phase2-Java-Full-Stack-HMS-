# Hospital Management System - Spring Boot API

Spring Boot REST API for the CRT Phase 2 Hospital Management System. The project exposes endpoints for system health, patient registration, doctor registration, doctor listing, appointment scheduling, appointment listing, and bed status.

## Current Status

- Spring Boot app starts successfully.
- HMS controller endpoints are available under `/api/hms`.
- MySQL JDBC support is included.
- App can fall back to in-memory mode if MySQL is unavailable.
- API smoke tests were added for the main Postman-style flow.
- Maven test and package checks are passing.
- Build artifact is generated at `target/demo-0.0.1-SNAPSHOT.jar`.

## Project Structure

```text
SpringbootCRT/demo
|-- pom.xml
|-- mvnw / mvnw.cmd
|-- src/main/java/demo/demo/DemoApplication.java
|-- src/main/java/demo/demo/hello.java
|-- src/main/java/HospitalManagementSystem/
|   |-- HMS.java
|   |-- AppointmentRow.java
|   |-- HospitalManagementService.java
|   `-- HospitalManagementController.java
|-- src/main/resources/application.properties
`-- src/test/java/demo/demo/DemoApplicationTests.java
```

## Requirements

- Java 21, matching the current `pom.xml`
- Maven Wrapper, already included as `mvnw.cmd`
- MySQL, if running with persistent database storage

Global Maven installation is not required. Use the Maven wrapper commands below.

## Database Configuration

The app reads database settings from environment variables:

```text
HMS_DB_URL
HMS_DB_USER
HMS_DB_PASSWORD
```

Defaults in code:

```text
HMS_DB_URL=jdbc:mysql://localhost:3306/hms_db
HMS_DB_USER=root
HMS_DB_PASSWORD=agrawalmm_3
```

For deployment, set these environment variables on the server instead of relying on local defaults.

If MySQL is unavailable, the app switches to in-memory mode. This is useful for local testing, but deployment should use the real MySQL connection.

## Run Locally

From `SpringbootCRT/demo`:

```powershell
.\mvnw.cmd spring-boot:run
```

Default local base URL:

```text
http://localhost:8080
```

## API Endpoints

### Health / Status

```http
GET /api/hms
GET /api/hms/health
GET /api/hms/beds
```

Example response:

```json
{
  "message": "Hospital Management System is running",
  "memoryMode": false,
  "totalBeds": 10,
  "occupiedBeds": 0,
  "availableBeds": 10
}
```

### Add Patient

```http
POST /api/hms/patients
Content-Type: application/json
```

```json
{
  "patientID": "P101",
  "name": "John Doe",
  "patientType": "Visitor",
  "rateOrFee": 500
}
```

Use `"Insider"` for admitted patients who occupy beds. Any other value defaults to visitor behavior.

### Add Doctor

```http
POST /api/hms/doctors
Content-Type: application/json
```

```json
{
  "docID": "D101",
  "name": "Dr. Smith",
  "specialization": "Cardiology"
}
```

### List Doctors

```http
GET /api/hms/doctors
```

### Schedule Appointment

```http
POST /api/hms/appointments
Content-Type: application/json
```

```json
{
  "patientID": "P101",
  "docID": "D101",
  "appointmentDate": "2026-05-23"
}
```

The appointment date must be in `YYYY-MM-DD` format.

### List Appointments

```http
GET /api/hms/appointments
```

### Legacy Hello Endpoint

```http
GET /hello
```

## Postman Check Flow

Use this order when checking manually in Postman:

1. `GET http://localhost:8080/api/hms/health`
2. `POST http://localhost:8080/api/hms/patients`
3. `POST http://localhost:8080/api/hms/doctors`
4. `GET http://localhost:8080/api/hms/doctors`
5. `POST http://localhost:8080/api/hms/appointments`
6. `GET http://localhost:8080/api/hms/appointments`
7. `GET http://localhost:8080/api/hms/beds`

No Postman collection file is currently committed in this module.

## Tests

Run:

```powershell
.\mvnw.cmd test
```

Current test coverage:

- Spring application context load
- HTTP smoke test for health, patient creation, doctor creation, doctor listing, appointment creation, appointment listing, and `/hello`

Latest local result:

```text
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Build Jar

Run:

```powershell
.\mvnw.cmd package
```

Generated artifact:

```text
target/demo-0.0.1-SNAPSHOT.jar
```

Latest local package check:

```text
BUILD SUCCESS
```

## Run Built Jar

```powershell
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

With deployment database variables:

```powershell
$env:HMS_DB_URL="jdbc:mysql://<host>:3306/hms_db"
$env:HMS_DB_USER="<user>"
$env:HMS_DB_PASSWORD="<password>"
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

## Deployment Readiness Notes

- Green signal from local API smoke test and package build.
- The app is configured for Java 21 and hosted port binding with `server.port=${PORT:8080}`.
- Configure `HMS_DB_URL`, `HMS_DB_USER`, and `HMS_DB_PASSWORD` on the deployment platform.
- Confirm MySQL database `hms_db` is reachable from the deployment server.
- After deployment, run the same Postman check flow against the deployed base URL.

## Deploy On Railway

This repository is a multi-folder workspace, so Railway must deploy only the Spring Boot module.

1. Push the latest code to GitHub.
2. Open Railway and create a new project.
3. Choose `Deploy from GitHub repo`.
4. Select this repository.
5. Open the backend service settings and set Root Directory to:

```text
/SpringbootCRT/demo
```

6. Add a MySQL service in the same Railway project.
7. Open the Spring Boot service variables and add:

```text
HMS_DB_URL=jdbc:mysql://<railway-mysql-host>:<railway-mysql-port>/<database-name>
HMS_DB_USER=<railway-mysql-user>
HMS_DB_PASSWORD=<railway-mysql-password>
```

8. Deploy the service.
9. In the service Networking tab, generate a public domain.
10. Verify:

```http
GET https://<your-railway-domain>/api/hms/health
```

Expected result:

```json
{
  "message": "Hospital Management System is running",
  "memoryMode": false
}
```

If `memoryMode` is `true`, the backend is running but Railway MySQL variables are not connected correctly.

## Vercel Usage

Do not deploy this Spring Boot backend on Vercel. Vercel is best used later for a frontend app. The frontend can call the Railway backend URL.
