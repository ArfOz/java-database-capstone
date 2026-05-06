// Import base API URL from config
import { API_BASE_URL } from "../config/config.js";

// Base endpoint for patient APIs
const PATIENT_API = API_BASE_URL + "/patient";


// =========================
// PATIENT SIGNUP
// =========================
export async function patientSignup(data) {

  try {
    const response = await fetch(`${PATIENT_API}/signup`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    });

    const result = await response.json();

    return {
      success: response.ok,
      message: result.message
    };

  } catch (error) {
    console.error("Signup error:", error);
    return {
      success: false,
      message: "Signup failed"
    };
  }
}


// =========================
// PATIENT LOGIN
// =========================
export async function patientLogin(data) {

  try {
    const response = await fetch(`${PATIENT_API}/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    });

    return response;

  } catch (error) {
    console.error("Login error:", error);
    return null;
  }
}


// =========================
// GET PATIENT DATA
// =========================
export async function getPatientData(token) {

  try {
    const response = await fetch(`${PATIENT_API}/me`, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${token}`
      }
    });

    return await response.json();

  } catch (error) {
    console.error("Error fetching patient data:", error);
    return null;
  }
}


// =========================
// GET PATIENT APPOINTMENTS
// =========================
export async function getPatientAppointments(id, token, user) {

  try {
    const url = `${PATIENT_API}/${user}/${id}/appointments`;

    const response = await fetch(url, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${token}`
      }
    });

    return await response.json();

  } catch (error) {
    console.error("Error fetching appointments:", error);
    return null;
  }
}


// =========================
// FILTER APPOINTMENTS
// =========================
export async function filterAppointments(condition, name, token) {

  try {
    const url = `${PATIENT_API}/filter?condition=${condition}&name=${name}`;

    const response = await fetch(url, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${token}`
      }
    });

    return await response.json();

  } catch (error) {
    console.error("Error filtering appointments:", error);
    return [];
  }
}