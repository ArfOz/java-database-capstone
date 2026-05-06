// Function to render the header dynamically based on user role
export function renderHeader() {

  const headerDiv = document.getElementById("header");
  if (!headerDiv) return;

  // Check if user is on homepage → clear session
  if (window.location.pathname.endsWith("/")) {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
  }

  // Get user role and token from localStorage
  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  // Validate session (if role exists but token is missing)
  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  let headerContent = "";

  // Common header structure (logo + navigation container)
  headerContent += `
    <div class="header-container">
      <h2 class="logo">Hospital System</h2>
      <nav class="nav-links">
  `;

  // Render header based on role
  if (role === "admin") {
    headerContent += `
      <button id="addDocBtn" class="adminBtn">Add Doctor</button>
      <a href="#" id="logoutBtn">Logout</a>
    `;
  }

  else if (role === "doctor") {
    headerContent += `
      <a href="/doctor/dashboard">Home</a>
      <a href="#" id="logoutBtn">Logout</a>
    `;
  }

  else if (role === "patient") {
    headerContent += `
      <a href="/login">Login</a>
      <a href="/signup">Sign Up</a>
    `;
  }

  else if (role === "loggedPatient") {
    headerContent += `
      <a href="/pages/patientDashboard.html">Home</a>
      <a href="#">Appointments</a>
      <a href="#" id="logoutPatientBtn">Logout</a>
    `;
  }

  // Close navigation container
  headerContent += `
      </nav>
    </div>
  `;

  // Inject generated HTML into the header div
  headerDiv.innerHTML = headerContent;

  // Attach event listeners to dynamically created elements
  attachHeaderButtonListeners();
}


// Attach event listeners to header buttons
function attachHeaderButtonListeners() {

  const addDocBtn = document.getElementById("addDocBtn");
  if (addDocBtn) {
    addDocBtn.addEventListener("click", () => {
      openModal("addDoctor");
    });
  }

  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", logout);
  }

  const logoutPatientBtn = document.getElementById("logoutPatientBtn");
  if (logoutPatientBtn) {
    logoutPatientBtn.addEventListener("click", logoutPatient);
  }
}


// Logout function for admin and doctor
function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userRole");
  window.location.href = "/";
}


// Logout function for patient (keeps role as "patient")
function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.setItem("userRole", "patient");
  window.location.href = "/pages/patientDashboard.html";
}