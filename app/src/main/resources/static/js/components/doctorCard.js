// Creates a reusable doctor card component for Admin and Patient dashboards
export function createDoctorCard(doctor) {

  // Main card container
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  // Get current user role from localStorage
  const role = localStorage.getItem("userRole");

  // =========================
  // Doctor Info Section
  // =========================
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  // Doctor name
  const name = document.createElement("h3");
  name.textContent = doctor.name;

  // Doctor specialization
  const specialization = document.createElement("p");
  specialization.textContent = `Specialty: ${doctor.specialization}`;

  // Doctor email
  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email}`;

  // Doctor availability (array → string)
  const availability = document.createElement("p");
  availability.textContent = `Available: ${doctor.availability?.join(", ")}`;

  // Append info elements
  infoDiv.appendChild(name);
  infoDiv.appendChild(specialization);
  infoDiv.appendChild(email);
  infoDiv.appendChild(availability);

  // =========================
  // Action Buttons Section
  // =========================
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  // =========================
  // ADMIN ACTIONS
  // =========================
  if (role === "admin") {

    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";

    removeBtn.addEventListener("click", async () => {

      // Confirm deletion action
      const confirmDelete = confirm("Are you sure you want to delete this doctor?");
      if (!confirmDelete) return;

      // Get auth token
      const token = localStorage.getItem("token");

      try {
        // Call API service to delete doctor
        await deleteDoctor(doctor.id, token);

        // Remove card from UI on success
        card.remove();

      } catch (err) {
        console.error("Delete failed:", err);
      }
    });

    actionsDiv.appendChild(removeBtn);
  }

  // =========================
  // PUBLIC PATIENT (NOT LOGGED IN)
  // =========================
  else if (role === "patient") {

    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";

    bookNow.addEventListener("click", () => {
      alert("Please log in to book an appointment.");
    });

    actionsDiv.appendChild(bookNow);
  }

  // =========================
  // LOGGED-IN PATIENT
  // =========================
  else if (role === "loggedPatient") {

    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";

    bookNow.addEventListener("click", async (e) => {

      try {
        const token = localStorage.getItem("token");

        // Fetch patient data from API
        const patientData = await getPatientData(token);

        // Open booking UI (modal / overlay)
        showBookingOverlay(e, doctor, patientData);

      } catch (err) {
        console.error("Booking failed:", err);
      }
    });

    actionsDiv.appendChild(bookNow);
  }

  // =========================
  // FINAL ASSEMBLY
  // =========================
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}