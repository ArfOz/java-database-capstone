package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.ApplicationService;
import com.project.back_end.services.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final ApplicationService applicationService;

    public DoctorController(DoctorService doctorService, ApplicationService applicationService) {
        this.applicationService = applicationService;
        this.doctorService = doctorService;
    }

    // availability
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable long doctorId,
            @PathVariable LocalDate date,
            @PathVariable String token) {

        if (!user.equalsIgnoreCase("doctor")) {
            throw new RuntimeException("Restricted area");
        }

        ResponseEntity<Map<String, Object>> auth = applicationService.validateToken(token, user);
        if (auth.getStatusCode() != HttpStatus.OK) return auth;

        Map<String, Object> response = new HashMap<>();
        List<String> doctorAvailabilityList = doctorService.getDoctorAvailability(doctorId, date);
        response.put("message", doctorAvailabilityList);
        return ResponseEntity.ok(response);
    }

    // get all doctors
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getDoctor() {
        try {
            Map<String, Object> response = new HashMap<>();
            List<Doctor> doctors = doctorService.getDoctors();
            response.put("doctors", doctors);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 5. saveDoctor
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> saveDoctor(
            @Valid @RequestBody Doctor doctor,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> auth = applicationService.validateToken(token, "admin");
        if (auth.getStatusCode() != HttpStatus.OK) return auth;

        Map<String, Object> response = new HashMap<>();
        int result = doctorService.saveDoctor(doctor);

        if (result == -1) {
            response.put("message", "Doctor already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else if (result == 1) {
            response.put("message", "Doctor saved successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        response.put("message", "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 6. doctorLogin
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@Valid @RequestBody Login login) {
        return applicationService.validateDoctorLogin(login);
    }

    // 7. updateDoctor
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, Object>> updateDoctor(
            @Valid @RequestBody Doctor doctor,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> auth = applicationService.validateToken(token, "admin");
        if (auth.getStatusCode() != HttpStatus.OK) return auth;

        Map<String, Object> response = new HashMap<>();
        int result = doctorService.updateDoctor(doctor);

        if (result == -1) {
            response.put("message", "Doctor not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else if (result == 1) {
            response.put("message", "Doctor updated successfully");
            return ResponseEntity.ok(response);
        }

        response.put("message", "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 8. deleteDoctor
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> deleteDoctor(
            @PathVariable long id,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> auth = applicationService.validateToken(token, "admin");
        if (auth.getStatusCode() != HttpStatus.OK) return auth;

        Map<String, Object> response = new HashMap<>();
        int result = doctorService.deleteDoctor(id);

        if (result == -1) {
            response.put("message", "Doctor not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else if (result == 1) {
            response.put("message", "Doctor deleted successfully");
            return ResponseEntity.ok(response);
        }

        response.put("message", "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 9. filter
    @GetMapping("/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {

        return ResponseEntity.ok(applicationService.filterDoctor(name, speciality, time));
    }
}