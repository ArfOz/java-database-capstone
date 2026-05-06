// Import modal handler
import { openModal } from "./components/modals.js";

// Import doctor services (API layer)
import {
  getDoctors,
  filterDoctors,
  saveDoctor
} from "./services/doctorServices.js";

// Import UI component for doctor card
import { createDoctorCard } from "./components/doctorCard.js";


// =========================
// INIT - LOAD PAGE
// =========================
document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();
});


// =========================
// OPEN ADD DOCTOR MODAL
// =========================
document.getElementById("addDocBtn")?.addEventListener("click", () => {
  openModal("addDoctor");
});


// =========================
// LOAD DOCTORS
// =========================
async function loadDoctorCards() {

  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  try {
    const doctors = await getDoctors();

    doctors.forEach(doctor => {
      const card = createDoctorCard(doctor);
      contentDiv.appendChild(card);
    });

  } catch (error) {
    console.error("Error loading doctors:", error);
    contentDiv.innerHTML = "<p>No doctors available.</p>";
  }
}


// =========================
// SEARCH + FILTER HANDLER
// =========================
function filterDoctorsOnChange() {

  const name = document.getElementById("searchBar").value;
  const time = document.getElementById("filterTime").value;
  const specialty = document.getElementById("filterSpecialty").value;

  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  filterDoctors(name, time, specialty)
    .then(doctors => {

      if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = "<p>No doctors found</p>";
        return;
      }

      doctors.forEach(doc => {
        contentDiv.appendChild(createDoctorCard(doc));
      });

    })
    .catch(err => {
      console.error(err);
      contentDiv.innerHTML = "<p>Error loading doctors</p>";
    });
}


// =========================
// EVENT LISTENERS
// =========================
document.getElementById("searchBar")?.addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime")?.addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty")?.addEventListener("change", filterDoctorsOnChange);


// =========================
// ADD DOCTOR (ADMIN)
// =========================
window.adminAddDoctor = async function (doctorData) {

  try {
    const token = localStorage.getItem("token");

    const response = await saveDoctor(doctorData, token);

    if (response.success) {
      alert("Doctor added successfully");
      loadDoctorCards();
    } else {
      alert(response.message);
    }

  } catch (error) {
    console.error(error);
    alert("Error adding doctor");
  }
};