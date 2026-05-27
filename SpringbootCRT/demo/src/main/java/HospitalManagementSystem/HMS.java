package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

abstract class Patient {
    String patientID;
    String name;

    Patient(String patientID, String name) {
        this.name = name;
        this.patientID = patientID;
    }

    abstract int calculateCharges(int days);
}

class Insider extends Patient {
    int bedRate;

    Insider(String patientID, String name, int bedRate) {
        super(patientID, name);
        this.bedRate = bedRate;
    }

    @Override
    int calculateCharges(int days) {
        return bedRate * days;
    }
}

class Visitor extends Patient {
    int consultFee;

    Visitor(String patientID, String name, int consultFee) {
        super(patientID, name);
        this.consultFee = consultFee;
    }

    @Override
    int calculateCharges(int days) {
        return consultFee * days;
    }
}

class Doctor {
    String docID;
    String name;
    String specialization;

    Doctor(String docID, String name, String specialization) {
        this.docID = docID;
        this.name = name;
        this.specialization = specialization;
    }
}

public class HMS {
    private final int totalBeds = 10;
    private final DatabaseConfig database;
    private boolean memoryMode;
    private final Map<String, Patient> memoryPatients = new LinkedHashMap<>();
    private final Map<String, Doctor> memoryDoctors = new LinkedHashMap<>();
    private final List<AppointmentRecord> memoryAppointments = new ArrayList<>();

    public HMS() {
        this(loadDatabaseConfig(null, null, null));
    }

    public HMS(String url, String user, String password) {
        this(loadDatabaseConfig(url, user, password));
    }

    private HMS(DatabaseConfig database) {
        this.database = database;
    }

    private static class AppointmentRecord {
        private final String patientID;
        private final String docID;
        private final String date;

        private AppointmentRecord(String patientID, String docID, String date) {
            this.patientID = patientID;
            this.docID = docID;
            this.date = date;
        }
    }

    private static DatabaseConfig loadDatabaseConfig(String configuredUrl, String configuredUser, String configuredPassword) {
        String rawUrl = firstPresent("JDBC_DATABASE_URL", "DATABASE_URL", "HMS_DB_URL", configuredUrl);
        String user = firstPresent("DB_USER", "HMS_DB_USER", configuredUser);
        String password = firstPresent("DB_PASSWORD", "HMS_DB_PASSWORD", configuredPassword);

        if (rawUrl == null) {
            return new DatabaseConfig("jdbc:postgresql://localhost:5432/hms_db",
                    user == null ? "postgres" : user,
                    password == null ? "" : password);
        }

        if (rawUrl.startsWith("jdbc:")) {
            return new DatabaseConfig(rawUrl,
                    user == null ? "" : user,
                    password == null ? "" : password);
        }

        if (rawUrl.startsWith("postgres://") || rawUrl.startsWith("postgresql://")) {
            return parsePostgresUrl(rawUrl, user, password);
        }

        return new DatabaseConfig(rawUrl,
                user == null ? "" : user,
                password == null ? "" : password);
    }

    private static DatabaseConfig parsePostgresUrl(String rawUrl, String configuredUser, String configuredPassword) {
        try {
            URI uri = new URI(rawUrl);
            String[] userInfo = uri.getUserInfo() == null ? new String[0] : uri.getUserInfo().split(":", 2);
            String user = configuredUser != null ? configuredUser : decode(userInfo.length > 0 ? userInfo[0] : "");
            String password = configuredPassword != null ? configuredPassword : decode(userInfo.length > 1 ? userInfo[1] : "");
            String path = uri.getPath() == null || uri.getPath().isBlank() ? "/hms_db" : uri.getPath();
            int port = uri.getPort() == -1 ? 5432 : uri.getPort();
            String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + path;
            if (uri.getQuery() != null && !uri.getQuery().isBlank()) {
                jdbcUrl += "?" + uri.getQuery();
            }
            return new DatabaseConfig(jdbcUrl, user, password);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid PostgreSQL DATABASE_URL.", e);
        }
    }

    private static String firstPresent(String... keys) {
        for (String keyOrValue : keys) {
            if (keyOrValue != null && !keyOrValue.isBlank() && keyOrValue.contains(":")) {
                return keyOrValue;
            }

            if (keyOrValue != null && !keyOrValue.isBlank() && !keyOrValue.matches("[A-Z0-9_]+")) {
                return keyOrValue;
            }

            if (keyOrValue != null && !keyOrValue.isBlank()) {
                String value = System.getenv(keyOrValue);
                if (value != null && !value.isBlank()) {
                    return value;
                }
            }
        }
        return null;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private Connection getConnection() throws SQLException {
        String driverClass = database.url().startsWith("jdbc:mysql:")
                ? "com.mysql.cj.jdbc.Driver"
                : "org.postgresql.Driver";
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC driver not found for " + database.url(), e);
        }
        return DriverManager.getConnection(database.url(), database.user(), database.password());
    }

    private boolean isMySql() {
        return database.url().startsWith("jdbc:mysql:");
    }

    public int getTotalBeds() {
        return totalBeds;
    }

    public boolean isMemoryMode() {
        return memoryMode;
    }

    public void initializeDatabase() {
        try (Connection conn = getConnection()) {
            String createPatientsTable = "CREATE TABLE IF NOT EXISTS patients ("
                    + "patientID VARCHAR(50) PRIMARY KEY, "
                    + "name VARCHAR(100) NOT NULL, "
                    + "patientType VARCHAR(20), "
                    + "rateOrFee INT)";

            String createDoctorsTable = "CREATE TABLE IF NOT EXISTS doctors ("
                    + "docID VARCHAR(50) PRIMARY KEY, "
                    + "name VARCHAR(100) NOT NULL, "
                    + "specialization VARCHAR(100))";

            String createAppointmentsTable = "CREATE TABLE IF NOT EXISTS appointments ("
                    + (isMySql() ? "appointmentID INT AUTO_INCREMENT PRIMARY KEY, " : "appointmentID SERIAL PRIMARY KEY, ")
                    + "patientID VARCHAR(50), "
                    + "docID VARCHAR(50), "
                    + "appointmentDate DATE, "
                    + "FOREIGN KEY (patientID) REFERENCES patients(patientID), "
                    + "FOREIGN KEY (docID) REFERENCES doctors(docID))";

            conn.createStatement().execute(createPatientsTable);
            conn.createStatement().execute(createDoctorsTable);
            conn.createStatement().execute(createAppointmentsTable);
            removeSmokeTestData(conn);
            memoryMode = false;
        } catch (SQLException e) {
            memoryMode = true;
            System.out.println("Database initialization skipped: " + e.getMessage());
            System.out.println("Using in-memory mode for this run. Set DATABASE_URL, DB_USER, and DB_PASSWORD to use PostgreSQL.");
        }
    }

    private void removeSmokeTestData(Connection conn) throws SQLException {
        try (PreparedStatement deleteAppointments = conn.prepareStatement(
                "DELETE FROM appointments WHERE docID IN (SELECT docID FROM doctors WHERE name = ?) "
                        + "OR patientID IN (SELECT patientID FROM patients WHERE name = ?)");
                PreparedStatement deleteDoctors = conn.prepareStatement("DELETE FROM doctors WHERE name = ?");
                PreparedStatement deletePatients = conn.prepareStatement("DELETE FROM patients WHERE name = ?")) {
            deleteAppointments.setString(1, "Smoke Doctor");
            deleteAppointments.setString(2, "Smoke Patient");
            deleteAppointments.executeUpdate();

            deleteDoctors.setString(1, "Smoke Doctor");
            deleteDoctors.executeUpdate();

            deletePatients.setString(1, "Smoke Patient");
            deletePatients.executeUpdate();
        }
    }

    public int getOccupiedBedsCount() {
        if (memoryMode) {
            int count = 0;
            for (Patient patient : memoryPatients.values()) {
                if (patient instanceof Insider) {
                    count++;
                }
            }
            return count;
        }

        String sql = "SELECT COUNT(*) FROM patients WHERE patientType = 'Insider'";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Unable to fetch bed count: " + e.getMessage());
        }
        return 0;
    }

    public String addPatient(Patient p) {
        if (p instanceof Insider && getOccupiedBedsCount() >= totalBeds) {
            return "Error: Registration failed. All beds are full.";
        }

        if (memoryMode) {
            if (memoryPatients.containsKey(p.patientID)) {
                return "Warning: Patient ID already exists.";
            }
            memoryPatients.put(p.patientID, p);
            return "Registered Patient: " + p.name + " (memory mode)";
        }

        String checkExistsSql = "SELECT patientID FROM patients WHERE patientID = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(checkExistsSql)) {
            pstmt.setString(1, p.patientID);
            if (pstmt.executeQuery().next()) {
                return "Warning: Patient ID already exists.";
            }
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }

        String insertSql = "INSERT INTO patients (patientID, name, patientType, rateOrFee) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, p.patientID);
            pstmt.setString(2, p.name);
            pstmt.setString(3, (p instanceof Insider) ? "Insider" : "Visitor");
            pstmt.setInt(4, (p instanceof Insider) ? ((Insider) p).bedRate : ((Visitor) p).consultFee);
            pstmt.executeUpdate();
            return "Registered Patient: " + p.name;
        } catch (SQLException e) {
            return "Database insertion error: " + e.getMessage();
        }
    }

    public String addDoctor(Doctor d) {
        if (memoryMode) {
            if (memoryDoctors.containsKey(d.docID)) {
                return "Warning: Doctor ID already exists.";
            }
            memoryDoctors.put(d.docID, d);
            return "Registered Doctor: " + d.name + " (memory mode)";
        }

        String checkExistsSql = "SELECT docID FROM doctors WHERE docID = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(checkExistsSql)) {
            pstmt.setString(1, d.docID);
            if (pstmt.executeQuery().next()) {
                return "Warning: Doctor ID already exists.";
            }
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }

        String sql = "INSERT INTO doctors (docID, name, specialization) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, d.docID);
            pstmt.setString(2, d.name);
            pstmt.setString(3, d.specialization);
            pstmt.executeUpdate();
            return "Registered Doctor: " + d.name;
        } catch (SQLException e) {
            return "Database insertion error: " + e.getMessage();
        }
    }

    public List<Doctor> getAllDoctors() {
        List<Doctor> list = new ArrayList<>();
        if (memoryMode) {
            list.addAll(memoryDoctors.values());
            return list;
        }

        String sql = "SELECT * FROM doctors";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Doctor(rs.getString("docID"), rs.getString("name"), rs.getString("specialization")));
            }
        } catch (SQLException e) {
            System.out.println("Unable to fetch doctors: " + e.getMessage());
        }
        return list;
    }

    public String scheduleAppointment(String patientID, String docID, String date) {
        if (memoryMode) {
            if (!memoryPatients.containsKey(patientID)) {
                return "Booking error: Patient ID not found.";
            }
            if (!memoryDoctors.containsKey(docID)) {
                return "Booking error: Doctor ID not found.";
            }
            memoryAppointments.add(new AppointmentRecord(patientID, docID, date));
            return "Appointment scheduled in memory mode.";
        }

        String sql = "INSERT INTO appointments (patientID, docID, appointmentDate) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patientID);
            pstmt.setString(2, docID);
            pstmt.setString(3, date);
            pstmt.executeUpdate();
            return "Appointment scheduled in database.";
        } catch (SQLException e) {
            return "Booking error: " + e.getMessage();
        }
    }

    public List<AppointmentRow> getAppointmentsData() {
        List<AppointmentRow> list = new ArrayList<>();
        if (memoryMode) {
            for (AppointmentRecord appointment : memoryAppointments) {
                Patient patient = memoryPatients.get(appointment.patientID);
                Doctor doctor = memoryDoctors.get(appointment.docID);
                if (patient != null && doctor != null) {
                    list.add(new AppointmentRow(patient.name, doctor.name, appointment.date));
                }
            }
            return list;
        }

        String sql = "SELECT p.name AS pName, d.name AS dName, a.appointmentDate "
                + "FROM appointments a "
                + "JOIN patients p ON a.patientID = p.patientID "
                + "JOIN doctors d ON a.docID = d.docID";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new AppointmentRow(rs.getString("pName"), rs.getString("dName"), rs.getString("appointmentDate")));
            }
        } catch (SQLException e) {
            System.out.println("Unable to fetch appointments: " + e.getMessage());
        }
        return list;
    }

    private record DatabaseConfig(String url, String user, String password) {
    }
}
