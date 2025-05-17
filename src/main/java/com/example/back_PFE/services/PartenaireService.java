package com.example.back_PFE.services;

import com.example.back_PFE.entities.Partenaire;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface PartenaireService {
    Partenaire addPartenaire(Partenaire partenaire);
    Partenaire updatePartenaire(Partenaire partenaire, long id);
    List<Partenaire> getPartenaire();
    void deletePartenaire(Long id);

    Partenaire acceptPartenaire(Long id);

    Partenaire refuserPartenaire(Long id);

    void sendNotificationToEmail(String recipientEmail, String message);

    void sendNotificationToEmailF(String recipientEmail, String message);

    void sendEmailAccept(Long id, String authorizationHeader);

    void sendEmailRefuse(Long id, String authorizationHeader);

    Optional<Partenaire> getPartenaireById(Long id);
}


