package demo.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {

	private final HttpClient httpClient = HttpClient.newHttpClient();

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
