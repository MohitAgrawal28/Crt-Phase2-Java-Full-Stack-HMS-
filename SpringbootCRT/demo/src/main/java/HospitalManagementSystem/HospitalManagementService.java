package HospitalManagementSystem;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HospitalManagementService {
    private final HMS hms;

    public HospitalManagementService(
            @Value("${spring.datasource.url:}") String databaseUrl,
            @Value("${spring.datasource.username:}") String databaseUser,
            @Value("${spring.datasource.password:}") String databasePassword) {
        this.hms = new HMS(databaseUrl, databaseUser, databasePassword);
        this.hms.initializeDatabase();
    }

    public SystemStatus status() {
        int occupiedBeds = hms.getOccupiedBedsCount();
        int totalBeds = hms.getTotalBeds();
        return new SystemStatus("Hospital Management System is running", hms.isMemoryMode(), totalBeds, occupiedBeds, totalBeds - occupiedBeds);
    }

    public OperationResult addPatient(PatientRequest request) {
        if (isBlank(request.patientID()) || isBlank(request.name())) {
            return OperationResult.error("Patient ID and name are required.");
        }
        String type = isBlank(request.patientType()) ? "Visitor" : request.patientType().trim();
        Patient patient = "Insider".equalsIgnoreCase(type)
                ? new Insider(request.patientID().trim(), request.name().trim(), request.rateOrFee())
                : new Visitor(request.patientID().trim(), request.name().trim(), request.rateOrFee());
        return OperationResult.fromMessage(hms.addPatient(patient));
    }

    public OperationResult addDoctor(DoctorRequest request) {
        if (isBlank(request.docID()) || isBlank(request.name())) {
            return OperationResult.error("Doctor ID and name are required.");
        }
        Doctor doctor = new Doctor(request.docID().trim(), request.name().trim(), safeTrim(request.specialization()));
        return OperationResult.fromMessage(hms.addDoctor(doctor));
    }

    public List<DoctorResponse> getDoctors() {
        return hms.getAllDoctors().stream()
                .map(doctor -> new DoctorResponse(doctor.docID, doctor.name, doctor.specialization))
                .toList();
    }

    public OperationResult scheduleAppointment(AppointmentRequest request) {
        if (isBlank(request.patientID()) || isBlank(request.docID()) || isBlank(request.appointmentDate())) {
            return OperationResult.error("Patient ID, doctor ID, and appointment date are required.");
        }
        try {
            LocalDate.parse(request.appointmentDate().trim());
        } catch (RuntimeException ex) {
            return OperationResult.error("Appointment date must be in YYYY-MM-DD format.");
        }
        return OperationResult.fromMessage(hms.scheduleAppointment(
                request.patientID().trim(),
                request.docID().trim(),
                request.appointmentDate().trim()));
    }

    public List<AppointmentRow> getAppointments() {
        return hms.getAppointmentsData();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    public record PatientRequest(String patientID, String name, String patientType, int rateOrFee) {
    }

    public record DoctorRequest(String docID, String name, String specialization) {
    }

    public record AppointmentRequest(String patientID, String docID, String appointmentDate) {
    }

    public record DoctorResponse(String docID, String name, String specialization) {
    }

    public record SystemStatus(String message, boolean memoryMode, int totalBeds, int occupiedBeds, int availableBeds) {
    }

    public record OperationResult(boolean success, String message) {
        static OperationResult fromMessage(String message) {
            String lower = message.toLowerCase();
            return new OperationResult(!lower.contains("error") && !lower.contains("warning"), message);
        }

        static OperationResult error(String message) {
            return new OperationResult(false, message);
        }
    }
}
