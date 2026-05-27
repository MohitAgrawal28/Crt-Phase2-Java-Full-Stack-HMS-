-- Hospital Management System - PostgreSQL Database Setup

-- =========================
-- PATIENTS TABLE
-- =========================
CREATE TABLE IF NOT EXISTS patients (
    patientID VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    patientType VARCHAR(20) DEFAULT 'Visitor',
    rateOrFee INT DEFAULT 0
);

-- =========================
-- DOCTORS TABLE
-- =========================
CREATE TABLE IF NOT EXISTS doctors (
    docID VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) DEFAULT ''
);

-- =========================
-- APPOINTMENTS TABLE
-- =========================
CREATE TABLE IF NOT EXISTS appointments (
    appointmentID SERIAL PRIMARY KEY,
    patientID VARCHAR(50) NOT NULL,
    docID VARCHAR(50) NOT NULL,
    appointmentDate DATE,

    CONSTRAINT fk_patient
        FOREIGN KEY (patientID)
        REFERENCES patients(patientID)
        ON DELETE CASCADE,

    CONSTRAINT fk_doctor
        FOREIGN KEY (docID)
        REFERENCES doctors(docID)
        ON DELETE CASCADE
);

-- =========================
-- INDEXES
-- =========================

CREATE INDEX IF NOT EXISTS idx_patient_type
ON patients(patientType);

CREATE INDEX IF NOT EXISTS idx_doctor_specialization
ON doctors(specialization);

CREATE INDEX IF NOT EXISTS idx_appointment_date
ON appointments(appointmentDate);
