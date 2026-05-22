-- Hospital Management System - Database Setup Script
-- Run this script in MySQL to initialize the HMS database

-- Create Database
CREATE DATABASE IF NOT EXISTS hms_db;
USE hms_db;

-- Create Patients Table
CREATE TABLE IF NOT EXISTS patients (
    patientID VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    patientType VARCHAR(20),
    rateOrFee INT,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Doctors Table
CREATE TABLE IF NOT EXISTS doctors (
    docID VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100),
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Appointments Table
CREATE TABLE IF NOT EXISTS appointments (
    appointmentID INT AUTO_INCREMENT PRIMARY KEY,
    patientID VARCHAR(50) NOT NULL,
    docID VARCHAR(50) NOT NULL,
    appointmentDate DATE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patientID) REFERENCES patients(patientID) ON DELETE CASCADE,
    FOREIGN KEY (docID) REFERENCES doctors(docID) ON DELETE CASCADE
);

-- Create Indexes for better performance
CREATE INDEX idx_patient_type ON patients(patientType);
CREATE INDEX idx_doctor_specialization ON doctors(specialization);
CREATE INDEX idx_appointment_date ON appointments(appointmentDate);

-- Display success message
SELECT 'Database initialized successfully!' AS Status;
