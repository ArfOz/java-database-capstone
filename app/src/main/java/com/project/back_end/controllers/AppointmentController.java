package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.ApplicationService;
import com.project.back_end.services.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final ApplicationService applicationService;

    public AppointmentController(AppointmentService appointmentService, ApplicationService applicationService) {
        this.appointmentService = appointmentService;
        this.applicationService = applicationService;
    }

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable LocalDate date,
            @PathVariable String patientName,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, Object>> auth = applicationService.validateToken(token, "doctor");

        if (auth.getStatusCode() != HttpStatus.OK) {
            return auth;
        }

        return appointmentService.getAppointment(patientName, date, token);

    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> bookAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token
    ) {

        ResponseEntity<Map<String, Object>> auth = applicationService.validateToken(token, "patient");

        if (auth.getStatusCode() != HttpStatus.OK) {
            return auth;
        }
        Map<String, Object> response = new HashMap<>();

        int validate = applicationService.validateAppointment(appointment);

        if (validate == -1) {
            response.put("message", "There is no doctor for this appointment");

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        } else if (validate == 0) {
            response.put("message", "Doctor is not possible");

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        } else if (validate == 1) {

            int res = appointmentService.bookAppointment(appointment);

            if (res == 1) {
                response.put("message", appointment);
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(response);
            } else {
                response.put("message", "Internal server error");
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(response);
            }
        }
        response.put("message", "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);


    }

    // 5. updateAppointment
    @PutMapping("/{token}")
    public ResponseEntity<String> updateAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> auth = applicationService.validateToken(token, "patient");
        if (auth.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        return appointmentService.updateAppointment(appointment);
    }

    // 6. cancelAppointment
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<String> cancelAppointment(
            @PathVariable Long id,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> auth = applicationService.validateToken(token, "patient");
        if (auth.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        Appointment appointment = new Appointment();
        appointment.setId(id);
        return appointmentService.cancelAppointment(appointment);
    }
}