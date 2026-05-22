package HospitalManagementSystem;

// Helper class to map database joins to the appointments table.
public class AppointmentRow {
    private final String patientName;
    private final String doctorName;
    private final String appointmentDate;

    public AppointmentRow(String patientName, String doctorName, String appointmentDate) {
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }
}
