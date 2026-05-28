# Hospital Management System - Monolithic Architecture

A full-stack Hospital Management System built using a monolithic architecture. The application manages patients, doctors, appointments, and hospital bed availability through a Spring Boot backend and a deployed frontend dashboard.

---

# Tech Stack

* **Frontend:** HTML, CSS, JavaScript, Vite
* **Backend:** Java, Spring Boot
* **API:** REST API
* **Database:** PostgreSQL on Render
* **Deployment:**

  * Frontend: Vercel
  * Backend: Render
* **Containerization:** Docker

---

# Repository Link

[Monolith Hospital Management System](https://github.com/MohitAgrawal28/Crt-Phase2-Java-Full-Stack-HMS-)

---

# Live Links

* **Frontend:** https://hms-frontend-render.vercel.app
* **Backend:** https://hms-backend-1cqc.onrender.com
* **Backend Health Check:** https://hms-backend-1cqc.onrender.com/api/hms/health

---

# Features

* Register patients
* Register doctors
* Schedule appointments
* View doctors
* View appointments
* Track total, occupied, and available beds
* Database-backed persistent storage
* Backend health check endpoint
* Responsive frontend dashboard

---

# Architecture

This project follows a monolithic architecture where the backend contains all core hospital management logic in a single Spring Boot application.

```text
Frontend Dashboard
       |
       | REST API Calls
       v
Spring Boot Backend
       |
       v
PostgreSQL Database
```

---

# Project Structure

```text
CRT Phase 2/
├── frontend/
│   ├── src/
│   ├── package.json
│   └── vercel.json
│
└── SpringbootCRT/
    └── demo/
        ├── src/main/java/
        ├── src/main/resources/
        ├── Dockerfile
        ├── pom.xml
        ├── railway.json
        └── DEPLOYMENT.md
```

---

# API Endpoints

## Health Check

```http
GET /api/hms/health
```

---

## Add Patient

```http
POST /api/hms/patients
```

### Request Body

```json
{
  "patientID": "P101",
  "name": "John Doe",
  "patientType": "Visitor",
  "rateOrFee": 500
}
```

---

## Add Doctor

```http
POST /api/hms/doctors
```

### Request Body

```json
{
  "docID": "D101",
  "name": "Dr. Smith",
  "specialization": "Cardiology"
}
```

---

## List Doctors

```http
GET /api/hms/doctors
```

---

## Schedule Appointment

```http
POST /api/hms/appointments
```

### Request Body

```json
{
  "patientID": "P101",
  "docID": "D101",
  "appointmentDate": "2026-05-28"
}
```

---

## List Appointments

```http
GET /api/hms/appointments
```

---

## Bed Status

```http
GET /api/hms/beds
```

---

# Database

The project uses PostgreSQL hosted on Render.

The backend reads database configuration from environment variables:

```text
DATABASE_URL
DB_USER
DB_PASSWORD
```

The application creates required tables automatically if they do not exist.

---

# Local Setup

# Backend

Go to the backend folder:

```bash
cd SpringbootCRT/demo
```

Run the Spring Boot application:

```bash
./mvnw spring-boot:run
```

### On Windows

```bash
mvnw.cmd spring-boot:run
```

Backend runs on:

```text
http://localhost:8080
```

---

# Frontend

Go to the frontend folder:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Run frontend:

```bash
npm run dev
```

Frontend runs on:

```text
http://localhost:5173
```

---

# Frontend Environment Variable

For deployed frontend, set:

```text
VITE_API_BASE_URL=https://hms-backend-1cqc.onrender.com
```

---

# Deployment

## Frontend Deployment

```text
Platform: Vercel
Root Directory: frontend
Build Command: npm run build
Output Directory: dist
```

---

## Backend Deployment

```text
Platform: Render
Root Directory: SpringbootCRT/demo
Dockerfile: SpringbootCRT/demo/Dockerfile
Health Check Path: /api/hms/health
```

---

# Health Check Verification

Open:

```text
https://hms-backend-1cqc.onrender.com/api/hms/health
```

Expected response includes:

```json
{
  "message": "Hospital Management System is running",
  "memoryMode": false
}
```

`memoryMode: false` means the backend is connected to PostgreSQL successfully.

---

# Docker Support

The backend service is containerized using Docker.

Example Dockerfile:

```dockerfile
FROM eclipse-temurin:17

WORKDIR /app

COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
```

---

# Notes

* Render free services may take some time to start after inactivity.
* UptimeRobot can be used to keep the backend active.
* The frontend communicates with the backend using REST API calls.
* Docker is used for backend containerized deployment.

---

# Future Enhancements

* Convert monolithic backend into microservices
* Add JWT Authentication
* Add API Gateway
* Add Eureka Service Discovery
* Add Docker Compose orchestration
* Add CI/CD pipeline
* Add Kubernetes deployment

---


