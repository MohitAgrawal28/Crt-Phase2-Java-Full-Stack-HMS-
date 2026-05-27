package HospitalManagementSystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class HMSApp extends JFrame {
    private final HMS databaseEngine;

    public HMSApp() {
        this.databaseEngine = new HMS();
        this.databaseEngine.initializeDatabase();
        setTitle("Hospital Management Control Center - "
                + (databaseEngine.isMemoryMode() ? "Memory Mode" : "Database Connected"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        if (databaseEngine.isMemoryMode()) {
            JOptionPane.showMessageDialog(this,
                    "Database is not connected. Data will not be saved after closing the app.\n"
                            + "Check HMS_DB_URL, HMS_DB_USER, and HMS_DB_PASSWORD.",
                    "Database connection warning",
                    JOptionPane.WARNING_MESSAGE);
        }
        showMainMenu();
    }

    private void showMainMenu() {
        JPanel root = createRootPanel();
        root.setLayout(new GridBagLayout());

        JLabel header = new JLabel("Hospital Management Interface", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 30));
        header.setForeground(new Color(30, 58, 138));

        JLabel databaseStatus = new JLabel(databaseEngine.isMemoryMode()
                ? "Memory mode: records are temporary"
                : "Database connected: " + databaseEngine.getDatabaseUrl(), SwingConstants.CENTER);
        databaseStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        databaseStatus.setForeground(databaseEngine.isMemoryMode() ? new Color(185, 28, 28) : new Color(22, 101, 52));

        JPanel menu = new JPanel(new GridBagLayout());
        menu.setBackground(Color.WHITE);
        menu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219)),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)));
        menu.setPreferredSize(new Dimension(430, 360));

        GridBagConstraints menuGbc = constraints();
        menuGbc.fill = GridBagConstraints.HORIZONTAL;
        menu.add(createMenuButton("Register New Patient", e -> renderAddPatientForm()), menuGbc);
        menuGbc.gridy++;
        menu.add(createMenuButton("Register New Doctor", e -> renderAddDoctorForm()), menuGbc);
        menuGbc.gridy++;
        menu.add(createMenuButton("Book Medical Appointment", e -> renderScheduleForm()), menuGbc);
        menuGbc.gridy++;
        menu.add(createMenuButton("View Dashboard Appointments", e -> renderAppointmentsDashboard()), menuGbc);
        menuGbc.gridy++;
        menu.add(createMenuButton("Live Bed Allocations", e -> renderBedStatusView()), menuGbc);
        menuGbc.gridy++;
        menu.add(createDangerButton("Shutdown Engine", e -> dispose()), menuGbc);

        GridBagConstraints rootGbc = constraints();
        rootGbc.insets = new Insets(0, 0, 22, 0);
        root.add(header, rootGbc);
        rootGbc.gridy = 1;
        rootGbc.insets = new Insets(0, 0, 16, 0);
        root.add(databaseStatus, rootGbc);
        rootGbc.gridy = 2;
        rootGbc.insets = new Insets(0, 0, 0, 0);
        root.add(menu, rootGbc);
        setContentPane(root);
        refresh();
    }

    private void renderAddPatientForm() {
        JPanel panel = createFormPanel("Patient Admission Entry");
        JTextField idInput = new JTextField(24);
        JTextField nameInput = new JTextField(24);
        JComboBox<String> typeDropdown = new JComboBox<>(new String[]{"Insider", "Visitor"});
        JTextField numericInput = new JTextField(24);
        JLabel status = new JLabel(" ");

        JPanel form = createGridForm();
        addRow(form, 0, "Unique Patient ID:", idInput);
        addRow(form, 1, "Full Name:", nameInput);
        addRow(form, 2, "Admission Class:", typeDropdown);
        addRow(form, 3, "Tariff Rate/Fee:", numericInput);

        JButton save = createMenuButton("Commit Registration", e -> {
            try {
                if (idInput.getText().trim().isEmpty() || nameInput.getText().trim().isEmpty()) {
                    setStatus(status, "Error: Patient ID and name are required.", false);
                    return;
                }
                int numericValue = Integer.parseInt(numericInput.getText().trim());
                Patient patient = "Insider".equals(typeDropdown.getSelectedItem())
                        ? new Insider(idInput.getText().trim(), nameInput.getText().trim(), numericValue)
                        : new Visitor(idInput.getText().trim(), nameInput.getText().trim(), numericValue);
                String feedback = databaseEngine.addPatient(patient);
                boolean ok = isSuccess(feedback);
                setStatus(status, feedback, ok);
                if (ok) {
                    idInput.setText("");
                    nameInput.setText("");
                    numericInput.setText("");
                }
            } catch (NumberFormatException ex) {
                setStatus(status, "Error: Ensure rate is a valid whole number.", false);
            }
        });

        panel.add(status, BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        panel.add(buttonBar(save, createMenuButton("Return To Menu", e -> showMainMenu())), BorderLayout.SOUTH);
        setContentPane(panel);
        refresh();
    }

    private void renderAddDoctorForm() {
        JPanel panel = createFormPanel("Medical Practitioner Registration");
        JTextField docIdInput = new JTextField(24);
        JTextField docNameInput = new JTextField(24);
        JTextField specInput = new JTextField(24);
        JLabel status = new JLabel(" ");

        JPanel form = createGridForm();
        addRow(form, 0, "Doctor License ID:", docIdInput);
        addRow(form, 1, "Full Practitioner Name:", docNameInput);
        addRow(form, 2, "Medical Department:", specInput);

        JButton save = createMenuButton("Register Practitioner", e -> {
            if (docIdInput.getText().trim().isEmpty() || docNameInput.getText().trim().isEmpty()) {
                setStatus(status, "Error: Doctor ID and name are required.", false);
                return;
            }
            Doctor doctor = new Doctor(docIdInput.getText().trim(), docNameInput.getText().trim(), specInput.getText().trim());
            String feedback = databaseEngine.addDoctor(doctor);
            boolean ok = isSuccess(feedback);
            setStatus(status, feedback, ok);
            if (ok) {
                docIdInput.setText("");
                docNameInput.setText("");
                specInput.setText("");
            }
        });

        panel.add(status, BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        panel.add(buttonBar(save, createMenuButton("Return To Menu", e -> showMainMenu())), BorderLayout.SOUTH);
        setContentPane(panel);
        refresh();
    }

    private void renderScheduleForm() {
        JPanel panel = createFormPanel("Book Appointment Sheet");
        JTextField patientInput = new JTextField(24);
        JComboBox<String> doctorDropdown = new JComboBox<>();
        JTextField dateInput = new JTextField(LocalDate.now().toString(), 24);
        JLabel status = new JLabel(" ");

        for (Doctor doctor : databaseEngine.getAllDoctors()) {
            doctorDropdown.addItem(doctor.docID + " - " + doctor.name);
        }

        JPanel form = createGridForm();
        addRow(form, 0, "Patient ID:", patientInput);
        addRow(form, 1, "Target Doctor:", doctorDropdown);
        addRow(form, 2, "Calendar Date (YYYY-MM-DD):", dateInput);

        JButton book = createMenuButton("Commit Reservation Slot", e -> {
            if (patientInput.getText().trim().isEmpty() || doctorDropdown.getSelectedItem() == null) {
                setStatus(status, "Error: Patient ID and doctor are required.", false);
                return;
            }
            try {
                LocalDate.parse(dateInput.getText().trim());
                String docId = doctorDropdown.getSelectedItem().toString().split(" - ")[0];
                String feedback = databaseEngine.scheduleAppointment(patientInput.getText().trim(), docId, dateInput.getText().trim());
                setStatus(status, feedback, isSuccess(feedback));
            } catch (Exception ex) {
                setStatus(status, "Error: Date must be in YYYY-MM-DD format.", false);
            }
        });

        panel.add(status, BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        panel.add(buttonBar(book, createMenuButton("Return To Menu", e -> showMainMenu())), BorderLayout.SOUTH);
        setContentPane(panel);
        refresh();
    }

    private void renderAppointmentsDashboard() {
        JPanel panel = createFormPanel("Active Operations Registry");
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Patient Name", "Doctor Assignment", "Scheduled Date"}, 0);
        for (AppointmentRow row : databaseEngine.getAppointmentsData()) {
            model.addRow(new Object[]{row.getPatientName(), row.getDoctorName(), row.getAppointmentDate()});
        }
        JTable table = new JTable(model);
        table.setRowHeight(28);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttonBar(createMenuButton("Main Menu Control Layer", e -> showMainMenu())), BorderLayout.SOUTH);
        setContentPane(panel);
        refresh();
    }

    private void renderBedStatusView() {
        JPanel panel = createFormPanel("Live Monitoring: Bed Space Metrics");
        int occupiedCount = databaseEngine.getOccupiedBedsCount();
        int maxCapacity = databaseEngine.getTotalBeds();
        int leftBeds = maxCapacity - occupiedCount;

        JPanel metrics = createGridForm();
        addRow(metrics, 0, "Total Configured Bed Assets:", new JLabel(String.valueOf(maxCapacity)));
        addRow(metrics, 1, "Occupied Overnights:", new JLabel(String.valueOf(occupiedCount)));
        JLabel remaining = new JLabel(String.valueOf(leftBeds));
        remaining.setFont(new Font("Segoe UI", Font.BOLD, 16));
        remaining.setForeground(leftBeds > 0 ? new Color(22, 163, 74) : new Color(220, 38, 38));
        addRow(metrics, 2, "Unallocated Available Slots:", remaining);

        panel.add(metrics, BorderLayout.CENTER);
        panel.add(buttonBar(createMenuButton("Back to Command Center", e -> showMainMenu())), BorderLayout.SOUTH);
        setContentPane(panel);
        refresh();
    }

    private JPanel createRootPanel() {
        JPanel root = new JPanel();
        root.setBackground(new Color(244, 246, 249));
        root.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        return root;
    }

    private JPanel createFormPanel(String titleText) {
        JPanel panel = createRootPanel();
        panel.setLayout(new BorderLayout(0, 18));
        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(30, 58, 138));
        panel.add(title, BorderLayout.PAGE_START);
        return panel;
    }

    private JPanel createGridForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(244, 246, 249));
        return form;
    }

    private void addRow(JPanel panel, int row, String labelText, Component field) {
        GridBagConstraints labelGbc = constraints();
        labelGbc.gridx = 0;
        labelGbc.gridy = row;
        labelGbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel(labelText), labelGbc);

        GridBagConstraints fieldGbc = constraints();
        fieldGbc.gridx = 1;
        fieldGbc.gridy = row;
        fieldGbc.fill = GridBagConstraints.HORIZONTAL;
        fieldGbc.weightx = 1;
        panel.add(field, fieldGbc);
    }

    private JPanel buttonBar(JButton... buttons) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        bar.setBackground(new Color(244, 246, 249));
        for (JButton button : buttons) {
            bar.add(button);
        }
        return bar;
    }

    private JButton createMenuButton(String label, java.awt.event.ActionListener listener) {
        JButton button = new JButton(label);
        button.setBackground(new Color(30, 58, 138));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(260, 38));
        button.addActionListener(listener);
        return button;
    }

    private JButton createDangerButton(String label, java.awt.event.ActionListener listener) {
        JButton button = createMenuButton(label, listener);
        button.setBackground(new Color(220, 38, 38));
        return button;
    }

    private GridBagConstraints constraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        return gbc;
    }

    private void setStatus(JLabel status, String text, boolean success) {
        status.setText(text);
        status.setForeground(success ? new Color(22, 101, 52) : new Color(185, 28, 28));
    }

    private boolean isSuccess(String text) {
        String lower = text.toLowerCase();
        return !lower.contains("error") && !lower.contains("warning");
    }

    private void refresh() {
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new HMSApp().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "HMS startup error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
