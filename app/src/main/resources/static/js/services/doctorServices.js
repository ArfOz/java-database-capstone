// Import base API URL from centralized config file
import { API_BASE_URL } from "../config/config.js";

// Base endpoint for all doctor-related API calls
const DOCTOR_API = API_BASE_URL + "/doctor";


// =========================
// GET ALL DOCTORS
// =========================
export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API);

    const data = await response.json();
    return data;

  } catch (error) {
    console.error("Error fetching doctors:", error);
    return [];
  }
}


// =========================
// DELETE DOCTOR (ADMIN ONLY)
// =========================
export async function deleteDoctor(id, token) {

  try {
    const response = await fetch(`${DOCTOR_API}/${id}`, {
      method: "DELETE",
      headers: {
        "Authorization": `Bearer ${token}`
      }
    });

    const data = await response.json();
    return data;

  } catch (error) {
    console.error("Error deleting doctor:", error);
    return { success: false, message: "Delete failed" };
  }
}


// =========================
// SAVE NEW DOCTOR (ADMIN ONLY)
// =========================
export async function saveDoctor(doctor, token) {

  try {
    const response = await fetch(DOCTOR_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      },
      body: JSON.stringify(doctor)
    });

    const data = await response.json();
    return data;

  } catch (error) {
    console.error("Error saving doctor:", error);
    return { success: false, message: "Save failed" };
  }
}


// =========================
// FILTER DOCTORS
// =========================
export async function filterDoctors(name, time, specialty) {

  try {
    const url = `${DOCTOR_API}/filter?name=${name}&time=${time}&specialty=${specialty}`;

    const response = await fetch(url);

    const data = await response.json();
    return data;

  } catch (error) {
    console.error("Error filtering doctors:", error);
    return [];
  }
}