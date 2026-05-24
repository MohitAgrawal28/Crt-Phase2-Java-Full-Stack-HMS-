package HospitalManagementSystem;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HospitalManagementController {
    private final HospitalManagementService service;

    public HospitalManagementController(HospitalManagementService service) {
        this.service = service;
    }

    @GetMapping("/")
    public HospitalManagementService.SystemStatus appRoot() {
        return service.status();
    }

    @GetMapping("/api/hms")
    public HospitalManagementService.SystemStatus root() {
        return service.status();
    }

    @GetMapping("/api/hms/health")
    public HospitalManagementService.SystemStatus health() {
        return service.status();
    }

    @PostMapping("/api/hms/patients")
    public ResponseEntity<HospitalManagementService.OperationResult> addPatient(
            @RequestBody HospitalManagementService.PatientRequest request) {
        return respond(service.addPatient(request));
    }

    @PostMapping("/api/hms/doctors")
    public ResponseEntity<HospitalManagementService.OperationResult> addDoctor(
            @RequestBody HospitalManagementService.DoctorRequest request) {
        return respond(service.addDoctor(request));
    }

    @GetMapping("/api/hms/doctors")
    public List<HospitalManagementService.DoctorResponse> getDoctors() {
        return service.getDoctors();
    }

    @PostMapping("/api/hms/appointments")
    public ResponseEntity<HospitalManagementService.OperationResult> scheduleAppointment(
            @RequestBody HospitalManagementService.AppointmentRequest request) {
        return respond(service.scheduleAppointment(request));
    }

    @GetMapping("/api/hms/appointments")
    public List<AppointmentRow> getAppointments() {
        return service.getAppointments();
    }

    @GetMapping("/api/hms/beds")
    public HospitalManagementService.SystemStatus beds() {
        return service.status();
    }

    private ResponseEntity<HospitalManagementService.OperationResult> respond(
            HospitalManagementService.OperationResult result) {
        return ResponseEntity.status(result.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(result);
    }
}
