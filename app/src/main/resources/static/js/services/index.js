// Import modal function to open login dialogs
import { openModal } from "../components/modals.js";

// Import base API URL from configuration
import { API_BASE_URL } from "../config/config.js";

// =========================
// API ENDPOINTS
// =========================
const ADMIN_API = API_BASE_URL + "/admin";
const DOCTOR_API = API_BASE_URL + "/doctor/login";


// =========================
// INIT (run after page load)
// =========================
window.onload = function () {

  // Select admin login button
  const adminBtn = document.getElementById("adminLogin");
  if (adminBtn) {
    adminBtn.addEventListener("click", () => {
      openModal("adminLogin");
    });
  }

  // Select doctor login button
  const doctorBtn = document.getElementById("doctorLogin");
  if (doctorBtn) {
    doctorBtn.addEventListener("click", () => {
      openModal("doctorLogin");
    });
  }
};


// =========================
// ADMIN LOGIN HANDLER
// =========================
window.adminLoginHandler = async function () {

  try {
    // Read input values
    const username = document.getElementById("adminUsername").value;
    const password = document.getElementById("adminPassword").value;

    const admin = { username, password };

    // Send request to backend
    const response = await fetch(ADMIN_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(admin)
    });

    // Handle response
    if (response.ok) {

      const data = await response.json();

      // Store token in localStorage
      localStorage.setItem("token", data.token);

      // Set role
      selectRole("admin");

      // Redirect if needed
      window.location.href = "/pages/adminDashboard.html";

    } else {
      alert("Invalid credentials!");
    }

  } catch (error) {
    console.error("Admin login error:", error);
    alert("Something went wrong!");
  }
};


// =========================
// DOCTOR LOGIN HANDLER
// =========================
window.doctorLoginHandler = async function () {

  try {
    // Read input values
    const email = document.getElementById("doctorEmail").value;
    const password = document.getElementById("doctorPassword").value;

    const doctor = { email, password };

    // Send request to backend
    const response = await fetch(DOCTOR_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(doctor)
    });

    // Handle response
    if (response.ok) {

      const data = await response.json();

      // Store token
      localStorage.setItem("token", data.token);

      // Set role
      selectRole("doctor");

      // Redirect
      window.location.href = "/doctor/dashboard";

    } else {
      alert("Invalid credentials!");
    }

  } catch (error) {
    console.error("Doctor login error:", error);
    alert("Something went wrong!");
  }
};


// =========================
// ROLE SELECT HELPER
// =========================
function selectRole(role) {
  localStorage.setItem("userRole", role);
}