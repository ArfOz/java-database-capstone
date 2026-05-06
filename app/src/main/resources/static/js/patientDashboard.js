// Import UI component
import { createDoctorCard } from "./components/doctorCard.js";

// Import modal
import { openModal } from "./components/modals.js";

// Import services
import {
  getDoctors,
  filterDoctors
} from "./services/doctorServices.js";

import {
  patientLogin,
  patientSignup
} from "./services/patientServices.js";


// =========================
// INIT
// =========================
document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();
});


// =========================
// LOAD DOCTORS
// =========================
async function loadDoctorCards() {

  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  const doctors = await getDoctors();

  doctors.forEach(doc => {
    contentDiv.appendChild(createDoctorCard(doc));
  });
}


// =========================
// MODAL TRIGGERS
// =========================
document.getElementById("patientSignup")?.addEventListener("click", () => {
  openModal("patientSignup");
});

document.getElementById("patientLogin")?.addEventListener("click", () => {
  openModal("patientLogin");
});


// =========================
// FILTER HANDLER
// =========================
function filterDoctorsOnChange() {

  const name = document.getElementById("searchBar").value;
  const time = document.getElementById("filterTime").value;
  const specialty = document.getElementById("filterSpecialty").value;

  const contentDiv = document.getElementById("content");

  filterDoctors(name, time, specialty)
    .then(doctors => {

      contentDiv.innerHTML = "";

      if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = "<p>No doctors found</p>";
        return;
      }

      doctors.forEach(doc => {
        contentDiv.appendChild(createDoctorCard(doc));
      });
    });
}


// =========================
// EVENT LISTENERS
// =========================
document.getElementById("searchBar")?.addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime")?.addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty")?.addEventListener("change", filterDoctorsOnChange);


// =========================
// PATIENT SIGNUP
// =========================
window.signupPatient = async function () {

  const data = {
    name: document.getElementById("name").value,
    email: document.getElementById("email").value,
    password: document.getElementById("password").value
  };

  const res = await patientSignup(data);

  if (res.success) {
    alert("Signup successful");
    location.reload();
  } else {
    alert(res.message);
  }
};


// =========================
// PATIENT LOGIN
// =========================
window.loginPatient = async function () {

  const data = {
    email: document.getElementById("loginEmail").value,
    password: document.getElementById("loginPassword").value
  };

  const response = await patientLogin(data);

  if (response && response.ok) {

    const result = await response.json();

    localStorage.setItem("token", result.token);
    localStorage.setItem("userRole", "loggedPatient");

    window.location.href = "/pages/patientDashboard.html";

  } else {
    alert("Login failed");
  }
};