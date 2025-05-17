package com.example.back_PFE.services;

import com.example.back_PFE.entities.Candidature;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CandidatureService {
    Candidature addCandidature(Candidature candidature, String email, Long offerId);
    List<Candidature> getCandidature();

    void deleteCandidature(Long id);

    void saveCV(Long id, MultipartFile file)throws IOException;

    void saveLettre(Long id, MultipartFile file)throws IOException;


    Optional<Candidature> getCandidatureById(Long id);

    Candidature acceptCandidature(Long id);

    Candidature RefuserCandidature(Long id);


    void sendNotificationToEmail(String recipientEmail, String message);

    void sendNotificationToEmailF(String recipientEmail, String message);

    void sendEmailAccept(Long id, String authorizationHeader);

    void sendEmailRefuse(Long id, String authorizationHeader);

    void updateCandidature(Long id, Candidature updatedCandidature);

   /* List<Candidature> getCandidaturesByUserId(Long userId);*/

    List<Candidature> getCandidaturesByOfferId(Long offerId);

    List<Candidature> getCandidaturesByCandidatId(Long candidatId);

    List<Candidature> getCandidaturesByUserId(Long userId);
}
