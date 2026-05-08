package com.project.back_end.services;


import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;
    private final AppointmentRepository appointmentRepository;

    public DoctorService(DoctorRepository doctorRepository,
                         TokenService tokenService,
                         AppointmentRepository appointmentRepository) {
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.appointmentRepository = appointmentRepository;
    }

    // ------------------------
    // CREATE
    // ------------------------

    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            boolean exists = doctorRepository.existsById(doctor.getId());
            if (exists) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public int updateDoctor(Doctor doctor) {
        try {
            if (!doctorRepository.existsById(doctor.getId())) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // ------------------------
    // READ
    // ------------------------

    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Doctor> findDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Doctor> findDoctorByName(String name) {
        return doctorRepository.findByNameLike(name);
    }

    // ------------------------
    // DELETE
    // ------------------------

    @Transactional
    public int deleteDoctor(Long doctorId) {
        try {
            if (!doctorRepository.existsById(doctorId)) {
                return -1;
            }

            appointmentRepository.deleteById(doctorId);
            doctorRepository.deleteById(doctorId);

            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // ------------------------
    // AUTH
    // ------------------------

    public String validateDoctor(String email) {
        Optional<Doctor> optionalDoctor = doctorRepository.findByEmail(email);

        if (optionalDoctor.isEmpty()) {
            return null;
        }

        Doctor doctor = optionalDoctor.get();


        return tokenService.generateToken(doctor.getEmail());
    }

    // ------------------------
    // AVAILABILITY (PLACEHOLDER)
    // ------------------------

    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null || doctor.getAvailableTimes() == null) {
            return Collections.emptyList();
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<String> bookedSlots = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, startOfDay, endOfDay)
                .stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString().substring(0, 5))
                .collect(Collectors.toList());

        return doctor.getAvailableTimes().stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }
}
