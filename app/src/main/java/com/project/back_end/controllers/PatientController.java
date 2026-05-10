package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.ApplicationService;
import com.project.back_end.services.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final ApplicationService applicationService;

    public PatientController(PatientService patientService, ApplicationService applicationService) {
        this.patientService = patientService;
        this.applicationService = applicationService;
    }

    // 3. getPatient
    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable String token) {

        ResponseEntity<Map<String, Object>> auth = applicationService.validateToken(token, "patient");
        if (auth.getStatusCode() != HttpStatus.OK) return auth;

        return patientService.getPatientDetails(token);
    }

    // 4. createPatient
    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> createPatient(@Valid @RequestBody Patient patient) {

        Map<String, Object> response = new HashMap<>();

        boolean exists = applicationService.validatePatient(patient);
        if (exists) {
            response.put("message", "Patient already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        int result = patientService.createPatient(patient);
        if (result == 1) {
            response.put("message", "Patient created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        response.put("message", "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 5. login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        return applicationService.validatePatientLogin(login);
    }

    // 6. getPatientAppointment
    @GetMapping("/{user}/{id}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointment(
            @PathVariable String user,
            @PathVariable Long id,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> auth = applicationService.validateToken(token, user);
        if (auth.getStatusCode() != HttpStatus.OK) return auth;

        return patientService.getPatientAppointment(id, token);
    }

    // 7. filterPatientAppointment
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointment(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> auth = applicationService.validateToken(token, "patient");
        if (auth.getStatusCode() != HttpStatus.OK) return auth;

        return applicationService.filterPatient(condition, name, token);
    }
}