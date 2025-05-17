package com.example.back_PFE.services;

import com.example.back_PFE.entities.Consultation;
import com.example.back_PFE.entities.Partenaire;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ConsultationService {
    List<LocalTime> getHeuresDispo(LocalDate date);

    Consultation consulter(Consultation consult);
    
    List<Consultation> getConsultation();
    
    Optional<Consultation> getConsultationById(Long id);

    
    Consultation acceptConsult(Long id);

    Consultation refuseconsult(Long id);

    void sendNotificationToEmail(String recipientEmail, String message);

    void sendNotificationToEmailF(String recipientEmail, String message);

    void sendEmailAccept(Long id, String authorizationHeader);

    void sendEmailRefuse(Long id, String authorizationHeader);


    void deleteConsult(Long id);
}
