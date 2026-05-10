package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ApplicationService {


    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public ApplicationService(TokenService tokenService,
                              AdminRepository adminRepository,
                              DoctorRepository doctorRepository,
                              PatientRepository patientRepository,
                              DoctorService doctorService,
                              PatientService patientService) {

        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }


    public ResponseEntity<Map<String, Object>> validateToken(String token, String user) {

        Map<String, Object> response = new HashMap<>();

        Boolean valid = tokenService.validateToken(token, user);

        if (!valid) {

            response.put("message", "Invalid or expired token");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }
        response.put("message", "Token valid");

        return ResponseEntity.ok(response);

    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Admin> optionalAdmin = adminRepository.findByUsername(receivedAdmin.getUsername());
            if (optionalAdmin.isEmpty()) {
                response.put("message", "Admin not found");
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);

            }
            Admin admin = optionalAdmin.get();

            if (!admin.getPassword()
                    .equals(receivedAdmin.getPassword())) {

                response.put("message", "Invalid password");

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }
            String token =
                    tokenService.generateToken(admin.getUsername());

            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("message", "Internal server error");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);

        }
    }

    public Map<String, Object> filterDoctor(
            String name,
            String specialty,
            String time
    ) {

        List<Doctor> doctors = doctorRepository.findDoctors(name, specialty, time);
        Map<String, Object> response = new HashMap<>();
        response.put("data", doctors);
        response.put("count", doctors.size());

        return response;

    }

    public int validateAppointment(Appointment appointment) {

        Optional<Doctor> optionalDoctor = doctorRepository.findById(appointment.getDoctor().getId());

        if (optionalDoctor.isEmpty()) {
            return -1;
        }
        Doctor doctor = optionalDoctor.get();

        String appointmentSlot = appointment.getAppointmentTime()
                .toLocalTime()
                .toString()
                .substring(0, 5);  // "09:00"

        List<String> availableSlots = doctorService.getDoctorAvailability(
                doctor.getId(),
                appointment.getAppointmentTime().toLocalDate()
        );
        if (!availableSlots.contains(appointmentSlot)) {
            return 0;
        }

        return 1;
    }


    public ResponseEntity<Map<String, String>> validateDoctorLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Doctor> optionalDoctor = doctorRepository.findByEmail(login.getEmail());
            if (optionalDoctor.isEmpty()) {
                response.put("message", "Doctor not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            Doctor doctor = optionalDoctor.get();
            if (!doctor.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(doctor.getEmail());
            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public boolean validatePatient(Patient patient) {

        Optional<Patient> optionalPatient = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());

        if (optionalPatient.isEmpty()) {
            return false;
        }

        return true;

    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {

        Map<String, String> response = new HashMap<>();
        try {
            Optional<Patient> optionalPatient = patientRepository.findByEmail(login.getEmail());
            if (optionalPatient.isEmpty()) {
                response.put("message", "Patient not found");
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);

            }
            Patient patientReal = optionalPatient.get();

            if (!patientReal.getPassword()
                    .equals(login.getPassword())) {

                response.put("message", "Invalid password");

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }
            String token =
                    tokenService.generateToken(patientReal.getName());

            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("message", "Internal server error");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);

        }

    }

    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {


        String email = tokenService.extractIdentifier(token);
        Long patientId = patientRepository.findByEmail(email).get().getId();

        boolean hasCondition = condition != null && !condition.isBlank();
        boolean hasName = name != null && !name.isBlank();

        if (hasCondition && hasName) return patientService.filterByDoctorAndCondition(condition, name, patientId);
        if (hasCondition) return patientService.filterByCondition(condition, patientId);
        if (hasName) return patientService.filterByDoctor(name, patientId);

        return patientService.getPatientAppointment(patientId, token);

    }


}
