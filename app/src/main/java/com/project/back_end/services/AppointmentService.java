package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final TokenService tokenService;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, TokenService tokenService, PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    int bookAppointment(Appointment appointment) {

        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }


    }

    @Transactional
    ResponseEntity<String> updateAppointment(Appointment appointment) {

        Map<String, Object> response = new HashMap<>();
        Boolean exist = this.validateAppointment(appointment);

        if (!exist) {
            throw new RuntimeException("Appointment not found");

        } else {
            appointmentRepository.save(appointment);
            return ResponseEntity.ok("Appointment updated successfully");
        }

    }

    @Transactional
    ResponseEntity<String> cancelAppointment(Appointment appointment) {
        Boolean exist = this.validateAppointment(appointment);
        if (!exist) {
            throw new RuntimeException("Appointment not found");
        }
        appointmentRepository.updateStatus(0, appointment.getId());
        return ResponseEntity.ok("Appointment cancelled successfully");
    }

    @Transactional
    ResponseEntity<Map<String, Object>> getAppointment(String pname, LocalDate date, String token) {

        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractIdentifier(token);
            Optional<Doctor> optionalDoctor = doctorRepository.findByEmail(email);
            if (optionalDoctor.isEmpty()) {
                response.put("message", "Doctor not found");

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(response);
            }
            Doctor doctor = optionalDoctor.get();
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(23, 59, 59);
            List<Appointment> appointments;

            if (pname != null && !pname.isBlank()) {
                appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                        doctor.getId(), pname, start, end
                );

            } else {
                appointments =
                        appointmentRepository
                                .findByDoctorIdAndAppointmentTimeBetween(
                                        doctor.getId(),
                                        start,
                                        end
                                );

            }
            response.put("appointments", appointments);
            return ResponseEntity.ok(response);


        } catch (Exception e) {
            response.put("message", "Internal server error");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }


    }


    private Boolean validateAppointment(Appointment appointment) {

        Optional<Appointment> exist = appointmentRepository.findById(appointment.getId());

        if (exist.isEmpty()) {
            return false;
        }
        return true;
    }

}
