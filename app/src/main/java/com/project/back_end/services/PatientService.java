package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository, AppointmentRepository appointmentRepository, TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }


    public int createPatient(Patient patient) {

        Optional<Patient> existingPatient = patientRepository.findByEmail(patient.getEmail());
        int response;
        if (existingPatient.isPresent()) {

            response = 0;
            return response;
        }
        patientRepository.save(patient);
        response = 1;
        return response;

    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        String email = tokenService.extractIdentifier(token);
        Long patientId = patientRepository.findByEmail(email).get().getId();

        if (!patientRepository.existsById(patientId)) {
            return ResponseEntity.notFound().build();
        }


        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);

        Map<String, Object> response = new HashMap<>();

        response.put("patientId", patientId);
        response.put("appointments", appointments);

        return ResponseEntity.ok(response);


    }

    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        int status;
        if (condition == "past") {
            status = 0;
        } else if (condition == "future") {
            status = 1;
        } else {
            return ResponseEntity.badRequest().build();
        }
        appointmentRepository.findByPatientIdAndStatus(id, status);
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("appointments", appointmentRepository.findByPatientIdAndStatus(id, status));
        return ResponseEntity.ok(response);

    }

    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        List<Appointment> appointments = appointmentRepository.findByDoctorNameAndPatientId(name, patientId);

        Map<String, Object> response = new HashMap<>();
        response.put("appointments", appointments);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        int status;
        if (condition == "past") {
            status = 0;
        } else if (condition == "future") {
            status = 1;
        } else {
            return ResponseEntity.badRequest().build();
        }

        Map<String, Object> response = new HashMap<>();

        List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(name, patientId, status);

        response.put("appointments", appointments);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        String email = tokenService.extractIdentifier(token);
        Long patientId = patientRepository.findByEmail(email).get().getId();

        if (!patientRepository.existsById(patientId)) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();

        Patient patient = patientRepository.findById(patientId).orElse(null);
        response.put("patientId", patient);
        return ResponseEntity.ok(response);
    }


// 9. **Handling Exceptions and Errors**:
//    - The service methods handle exceptions using try-catch blocks and log any issues that occur. If an error occurs during database operations, the service responds with appropriate HTTP status codes (e.g., `500 Internal Server Error`).
//    - Instruction: Ensure that error handling is consistent across the service, with proper logging and meaningful error messages returned to the client.

// 10. **Use of DTOs (Data Transfer Objects)**:
//    - The service uses `AppointmentDTO` to transfer appointment-related data between layers. This ensures that sensitive or unnecessary data (e.g., password or private patient information) is not exposed in the response.
//    - Instruction: Ensure that DTOs are used appropriately to limit the exposure of internal data and only send the relevant fields to the client.


}
