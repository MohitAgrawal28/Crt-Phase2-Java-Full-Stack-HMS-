package demo.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {

	private final HttpClient httpClient = HttpClient.newHttpClient();
	private String createdPatientId;
	private String createdDoctorId;

	@Value("${local.server.port}")
	private int port;

	@Test
	void contextLoads() {
	}

	@Test
	void hospitalManagementApiSmokeTest() throws Exception {
		String suffix = UUID.randomUUID().toString().substring(0, 8);
		String patientId = "P" + suffix;
		String doctorId = "D" + suffix;
		createdPatientId = patientId;
		createdDoctorId = doctorId;

		HttpResponse<String> health = get("/api/hms/health");
		assertThat(health.statusCode()).isEqualTo(200);
		assertThat(health.body()).contains("Hospital Management System is running");

		HttpResponse<String> patient = post("/api/hms/patients",
				"{\"patientID\":\"" + patientId + "\",\"name\":\"Smoke Patient\",\"patientType\":\"Visitor\",\"rateOrFee\":500}");
		assertThat(patient.statusCode()).isEqualTo(200);
		assertThat(patient.body()).contains("\"success\":true");

		HttpResponse<String> doctor = post("/api/hms/doctors",
				"{\"docID\":\"" + doctorId + "\",\"name\":\"Smoke Doctor\",\"specialization\":\"General\"}");
		assertThat(doctor.statusCode()).isEqualTo(200);
		assertThat(doctor.body()).contains("\"success\":true");

		HttpResponse<String> doctors = get("/api/hms/doctors");
		assertThat(doctors.statusCode()).isEqualTo(200);
		assertThat(doctors.body()).contains(doctorId, "Smoke Doctor");

		HttpResponse<String> appointment = post("/api/hms/appointments",
				"{\"patientID\":\"" + patientId + "\",\"docID\":\"" + doctorId
						+ "\",\"appointmentDate\":\"2026-05-23\"}");
		assertThat(appointment.statusCode()).isEqualTo(200);
		assertThat(appointment.body()).contains("\"success\":true");

		HttpResponse<String> appointments = get("/api/hms/appointments");
		assertThat(appointments.statusCode()).isEqualTo(200);
		assertThat(appointments.body()).contains("Smoke Patient", "Smoke Doctor", "2026-05-23");

		HttpResponse<String> hello = get("/hello");
		assertThat(hello.statusCode()).isEqualTo(200);
		assertThat(hello.body()).contains("Hello world");
	}

	@AfterEach
	void cleanUpSmokeTestData() {
		if (createdPatientId == null || createdDoctorId == null) {
			return;
		}

		try (Connection conn = DriverManager.getConnection(config("HMS_DB_URL", "jdbc:mysql://localhost:3306/hms_db"),
				config("HMS_DB_USER", "root"), config("HMS_DB_PASSWORD", "agrawalmm_3"))) {
			deleteById(conn, "DELETE FROM appointments WHERE patientID = ? OR docID = ?", createdPatientId, createdDoctorId);
			deleteById(conn, "DELETE FROM patients WHERE patientID = ?", createdPatientId);
			deleteById(conn, "DELETE FROM doctors WHERE docID = ?", createdDoctorId);
		} catch (SQLException ignored) {
			// The service may be running in memory mode when MySQL is unavailable.
		}
	}

	private void deleteById(Connection conn, String sql, String... values) throws SQLException {
		try (PreparedStatement statement = conn.prepareStatement(sql)) {
			for (int i = 0; i < values.length; i++) {
				statement.setString(i + 1, values[i]);
			}
			statement.executeUpdate();
		}
	}

	private String config(String key, String defaultValue) {
		String value = System.getenv(key);
		return value == null || value.isBlank() ? defaultValue : value;
	}

	private HttpResponse<String> get(String path) throws Exception {
		HttpRequest request = HttpRequest.newBuilder(uri(path)).GET().build();
		return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private HttpResponse<String> post(String path, String body) throws Exception {
		HttpRequest request = HttpRequest.newBuilder(uri(path))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(body))
				.build();
		return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private URI uri(String path) {
		return URI.create("http://localhost:" + port + path);
	}

}
