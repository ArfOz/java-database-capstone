// Import services
import { getAllAppointments } from "./services/appointmentRecordService.js";

// Import UI component
import { createPatientRow } from "./components/patientRows.js";


// =========================
// GLOBAL STATE
// =========================
let selectedDate = new Date().toISOString().split("T")[0];
let patientName = null;
const token = localStorage.getItem("token");

const tableBody = document.getElementById("patientTableBody");


// =========================
// INIT
// =========================
document.addEventListener("DOMContentLoaded", () => {
  loadAppointments();
});


// =========================
// SEARCH INPUT
// =========================
document.getElementById("searchBar")?.addEventListener("input", (e) => {
  patientName = e.target.value || null;
  loadAppointments();
});


// =========================
// TODAY BUTTON
// =========================
document.getElementById("todayButton")?.addEventListener("click", () => {
  selectedDate = new Date().toISOString().split("T")[0];
  document.getElementById("datePicker").value = selectedDate;
  loadAppointments();
});


// =========================
// DATE PICKER
// =========================
document.getElementById("datePicker")?.addEventListener("change", (e) => {
  selectedDate = e.target.value;
  loadAppointments();
});


// =========================
// LOAD APPOINTMENTS
// =========================
async function loadAppointments() {

  tableBody.innerHTML = "";

  try {
    const appointments = await getAllAppointments(selectedDate, patientName, token);

    if (!appointments || appointments.length === 0) {
      tableBody.innerHTML = "<tr><td colspan='5'>No Appointments Found</td></tr>";
      return;
    }

    appointments.forEach(app => {
      const row = createPatientRow(app);
      tableBody.appendChild(row);
    });

  } catch (error) {
    console.error(error);
    tableBody.innerHTML = "<tr><td colspan='5'>Error loading data</td></tr>";
  }
}