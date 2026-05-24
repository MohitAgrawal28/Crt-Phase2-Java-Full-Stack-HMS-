-- Hospital Management System - PostgreSQL Database Setup

-- =========================
-- PATIENTS TABLE
-- =========================
CREATE TABLE IF NOT EXISTS patients (
    patient_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    patient_type VARCHAR(20) DEFAULT 'Visitor',
    rate_or_fee INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- DOCTORS TABLE
-- =========================
CREATE TABLE IF NOT EXISTS doctors (
    doc_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- APPOINTMENTS TABLE
-- =========================
CREATE TABLE IF NOT EXISTS appointments (
    appointment_id SERIAL PRIMARY KEY,
    patient_id VARCHAR(50) NOT NULL,
    doc_id VARCHAR(50) NOT NULL,
    appointment_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_doctor
        FOREIGN KEY (doc_id)
        REFERENCES doctors(doc_id)
        ON DELETE CASCADE
);

-- =========================
-- INDEXES
-- =========================

CREATE INDEX IF NOT EXISTS idx_patient_type
ON patients(patient_type);

CREATE INDEX IF NOT EXISTS idx_doctor_specialization
ON doctors(specialization);

CREATE INDEX IF NOT EXISTS idx_appointment_date
ON appointments(appointment_date);