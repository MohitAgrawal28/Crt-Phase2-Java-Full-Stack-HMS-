import "./styles.css";

const configuredBaseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
const API_BASE_URL = configuredBaseUrl.replace(/\/$/, "");

const app = document.querySelector("#app");

app.innerHTML = `
  <header class="shell header">
    <div class="brand">
      <div class="brand-mark" aria-hidden="true">+</div>
      <div>
        <h1>Hospital Management System</h1>
        <p>Operations dashboard for patients, doctors, appointments, and bed capacity.</p>
      </div>
    </div>
    <div class="status-pill">
      <span id="statusDot" class="status-dot"></span>
      <span id="statusText">Connecting</span>
    </div>
  </header>

  <main class="shell main-grid">
    <section class="metrics" aria-label="Hospital status">
      <article class="metric">
        <span>Total beds</span>
        <strong id="totalBeds">0</strong>
      </article>
      <article class="metric danger">
        <span>Occupied</span>
        <strong id="occupiedBeds">0</strong>
      </article>
      <article class="metric success">
        <span>Available</span>
        <strong id="availableBeds">0</strong>
      </article>
      <article class="metric accent">
        <span>Storage</span>
        <strong id="storageMode">-</strong>
      </article>
    </section>

    <section id="memoryNotice" class="banner" hidden>
      Backend is running in memory mode. Connect Railway PostgreSQL before using this in production.
    </section>

    <section class="workspace">
      <div class="forms-column">
        <article class="panel">
          <div class="panel-head">
            <h2>Register Patient</h2>
          </div>
          <form id="patientForm" autocomplete="off">
            <div class="field-grid">
              <label>Patient ID<input name="patientID" required placeholder="P102" /></label>
              <label>Name<input name="name" required placeholder="Aarav Sharma" /></label>
            </div>
            <div class="field-grid">
              <label>Patient Type
                <select name="patientType">
                  <option value="Insider">Insider</option>
                  <option value="Visitor">Visitor</option>
                </select>
              </label>
              <label>Rate / Fee<input name="rateOrFee" type="number" min="0" value="500" required /></label>
            </div>
            <button type="submit">Register Patient</button>
          </form>
        </article>

        <article class="panel">
          <div class="panel-head">
            <h2>Register Doctor</h2>
          </div>
          <form id="doctorForm" autocomplete="off">
            <div class="field-grid">
              <label>Doctor ID<input name="docID" required placeholder="D102" /></label>
              <label>Name<input name="name" required placeholder="Dr. Mehta" /></label>
            </div>
            <label>Specialization<input name="specialization" placeholder="Cardiology" /></label>
            <button type="submit">Register Doctor</button>
          </form>
        </article>

        <article class="panel">
          <div class="panel-head">
            <h2>Schedule Appointment</h2>
          </div>
          <form id="appointmentForm" autocomplete="off">
            <div class="field-grid">
              <label>Patient ID<input name="patientID" required placeholder="P102" /></label>
              <label>Doctor ID<input name="docID" required placeholder="D102" /></label>
            </div>
            <label>Date<input name="appointmentDate" type="date" required /></label>
            <button type="submit">Schedule Appointment</button>
          </form>
        </article>
      </div>

      <div class="tables-column">
        <article class="panel">
          <div class="panel-head">
            <h2>Doctors</h2>
            <button class="secondary" type="button" id="refreshDoctors">Refresh</button>
          </div>
          <div id="doctorsEmpty" class="empty">No doctors registered yet.</div>
          <div class="table-scroll">
            <table id="doctorsTable" hidden>
              <thead>
                <tr><th>Doctor ID</th><th>Name</th><th>Specialization</th></tr>
              </thead>
              <tbody></tbody>
            </table>
          </div>
        </article>

        <article class="panel">
          <div class="panel-head">
            <h2>Appointments</h2>
            <button class="secondary" type="button" id="refreshAppointments">Refresh</button>
          </div>
          <div id="appointmentsEmpty" class="empty">No appointments scheduled yet.</div>
          <div class="table-scroll">
            <table id="appointmentsTable" hidden>
              <thead>
                <tr><th>Patient</th><th>Doctor</th><th>Date</th></tr>
              </thead>
              <tbody></tbody>
            </table>
          </div>
        </article>
      </div>
    </section>
  </main>

  <div id="toast" class="toast" role="status" aria-live="polite"></div>
`;

const routes = {
  health: "/api/hms/health",
  patients: "/api/hms/patients",
  doctors: "/api/hms/doctors",
  appointments: "/api/hms/appointments"
};

const $ = (id) => document.getElementById(id);
const toast = $("toast");

function endpoint(path) {
  return `${API_BASE_URL}${path}`;
}

function showToast(text, type = "success") {
  toast.textContent = text;
  toast.className = `toast show ${type}`;
  window.clearTimeout(showToast.timer);
  showToast.timer = window.setTimeout(() => {
    toast.className = "toast";
  }, 3600);
}

async function request(path, options = {}) {
  const response = await fetch(endpoint(path), {
    headers: { "Content-Type": "application/json" },
    cache: "no-store",
    ...options
  });
  const text = await response.text();
  const body = text ? JSON.parse(text) : {};
  if (!response.ok || body.success === false) {
    throw new Error(body.message || `Request failed with ${response.status}`);
  }
  return body;
}

function toArray(response) {
  return Array.isArray(response) ? response : response.value || [];
}

function formData(form) {
  return Object.fromEntries(new FormData(form).entries());
}

function escapeHtml(value) {
  return String(value ?? "").replace(/[&<>"']/g, (char) => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;",
    "'": "&#039;"
  })[char]);
}

function setBusy(form, busy) {
  form.querySelectorAll("button, input, select").forEach((control) => {
    control.disabled = busy;
  });
}

async function submitForm(form, path, payload, afterReset) {
  setBusy(form, true);
  try {
    const result = await request(path, { method: "POST", body: JSON.stringify(payload) });
    form.reset();
    afterReset?.();
    showToast(result.message);
  } catch (error) {
    showToast(error.message, "error");
  } finally {
    setBusy(form, false);
    await refreshAll();
  }
}

async function loadStatus() {
  try {
    const status = await request(routes.health);
    $("totalBeds").textContent = status.totalBeds;
    $("occupiedBeds").textContent = status.occupiedBeds;
    $("availableBeds").textContent = status.availableBeds;
    $("storageMode").textContent = status.memoryMode ? "Memory" : "Database";
    $("statusText").textContent = "API online";
    $("statusDot").classList.add("online");
    $("memoryNotice").hidden = !status.memoryMode;
  } catch (error) {
    $("statusText").textContent = "API offline";
    $("statusDot").classList.remove("online");
    $("memoryNotice").hidden = false;
    showToast(error.message, "error");
  }
}

async function loadDoctors() {
  const doctors = toArray(await request(routes.doctors));
  const table = $("doctorsTable");
  const empty = $("doctorsEmpty");
  table.querySelector("tbody").innerHTML = doctors.map((doctor) => `
    <tr>
      <td>${escapeHtml(doctor.docID)}</td>
      <td>${escapeHtml(doctor.name)}</td>
      <td>${escapeHtml(doctor.specialization || "-")}</td>
    </tr>
  `).join("");
  table.hidden = doctors.length === 0;
  empty.hidden = doctors.length !== 0;
}

async function loadAppointments() {
  const appointments = toArray(await request(routes.appointments));
  const table = $("appointmentsTable");
  const empty = $("appointmentsEmpty");
  table.querySelector("tbody").innerHTML = appointments.map((appointment) => `
    <tr>
      <td>${escapeHtml(appointment.patientName)}</td>
      <td>${escapeHtml(appointment.doctorName)}</td>
      <td>${escapeHtml(appointment.appointmentDate)}</td>
    </tr>
  `).join("");
  table.hidden = appointments.length === 0;
  empty.hidden = appointments.length !== 0;
}

async function refreshAll() {
  await loadStatus();
  await Promise.all([loadDoctors(), loadAppointments()]);
}

$("patientForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  const payload = formData(event.currentTarget);
  payload.rateOrFee = Number(payload.rateOrFee);
  await submitForm(event.currentTarget, routes.patients, payload, () => {
    event.currentTarget.rateOrFee.value = 500;
  });
});

$("doctorForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  await submitForm(event.currentTarget, routes.doctors, formData(event.currentTarget));
});

$("appointmentForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  await submitForm(event.currentTarget, routes.appointments, formData(event.currentTarget));
});

$("refreshDoctors").addEventListener("click", () => refreshAll().catch((error) => showToast(error.message, "error")));
$("refreshAppointments").addEventListener("click", () => refreshAll().catch((error) => showToast(error.message, "error")));

refreshAll().catch((error) => showToast(error.message, "error"));
