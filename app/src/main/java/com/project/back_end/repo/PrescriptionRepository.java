package com.project.back_end.repo;

import com.project.back_end.models.Prescription;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@Document(collection = "prescriptions")
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {


    List<Prescription> findByAppointmentId(Long appointmentId);


}

