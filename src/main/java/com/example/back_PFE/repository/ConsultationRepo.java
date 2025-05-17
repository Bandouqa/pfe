package com.example.back_PFE.repository;

import com.example.back_PFE.entities.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Repository
public interface ConsultationRepo extends JpaRepository<Consultation, Long> {
    boolean existsByDateAndHeure(LocalDate date, LocalTime heure);
    List<Consultation> findByDate(LocalDate date);
}
