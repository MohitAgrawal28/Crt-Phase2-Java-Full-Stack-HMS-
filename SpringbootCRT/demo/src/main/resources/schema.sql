-- Hospital Management System - PostgreSQL Database Setup
-- Run this script in PostgreSQL to initialize the HMS database

-- Create Patients Table
CREATE TABLE IF NOT EXISTS patients (
    patient_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    patient_type VARCHAR(20) DEFAULT 'Visitor',
    rate_or_fee INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Doctors Table
CREATE TABLE IF NOT EXISTS doctors (
    doc_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Appointments Table
CREATE TABLE IF NOT EXISTS appointments (
    appointment_id SERIAL PRIMARY KEY,
    patient_id VARCHAR(50) NOT NULL,
    doc_id VARCHAR(50) NOT NULL,
    appointment_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doc_id) REFERENCES doctors(doc_id) ON DELETE CASCADE
);

-- Create Indexes for better performance (only if table exists)
DO $$ 
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'patients') THEN
        CREATE INDEX IF NOT EXISTS idx_patient_type ON patients(patient_type);
    END IF;
    
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'doctors') THEN
        CREATE INDEX IF NOT EXISTS idx_doctor_specialization ON doctors(specialization);
    END IF;
    
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'appointments') THEN
        CREATE INDEX IF NOT EXISTS idx_appointment_date ON appointments(appointment_date);
    END IF;
END $$;
