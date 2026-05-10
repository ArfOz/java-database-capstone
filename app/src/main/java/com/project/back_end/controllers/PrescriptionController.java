package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.ApplicationService;
import com.project.back_end.services.PrescriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    final private PrescriptionService prescriptionService;
    final private ApplicationService applicationService;

    public PrescriptionController(PrescriptionService prescriptionService, ApplicationService applicationService) {
        this.applicationService = applicationService;
        this.prescriptionService = prescriptionService;
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @PathVariable String token,
            @RequestBody Prescription prescription
    ) {
        ResponseEntity<Map<String, Object>> auth = applicationService.validateToken(token, "doctor");
        if (auth.getStatusCode() != HttpStatus.OK) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Restricted area");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {

            return prescriptionService.savePrescription(prescription);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable long appointmentId,
            @PathVariable String token

    ) {
        ResponseEntity<Map<String, Object>> auth = applicationService.validateToken(token, "doctor");
        if (auth.getStatusCode() != HttpStatus.OK) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Restricted area");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        try {

            return prescriptionService.getPrescription(appointmentId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
    

}
