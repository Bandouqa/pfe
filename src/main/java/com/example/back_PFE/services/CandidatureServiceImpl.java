package com.example.back_PFE.services;
import com.example.back_PFE.entities.Candidat;
import com.example.back_PFE.entities.Candidature;
import com.example.back_PFE.entities.Offre;
import com.example.back_PFE.entities.Status;
import com.example.back_PFE.jwt.JwtUtil;
import com.example.back_PFE.repository.CandidatRepo;
import com.example.back_PFE.repository.CandidatureRepo;
import com.example.back_PFE.repository.OffreRepo;
import com.example.back_PFE.user.User;
import com.example.back_PFE.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;

@Service
public class CandidatureServiceImpl implements CandidatureService {

    private final CandidatureRepo candidatureRepo;
    private final OffreRepo offreRepo;
    private final UserRepository userRepository;
    private final String uploadDir = "C:/Users/neder/Desktop/cv/uploads";
    @Autowired
    private JavaMailSender javaMailSender;
    private final JwtUtil jwtUtil;
    private final CandidatRepo candidatRepo;

    @Autowired
    public CandidatureServiceImpl(CandidatureRepo candidatureRepo, OffreRepo offreRepo, UserRepository userRepository ,JwtUtil jwtUtil,CandidatRepo candidatRepo) {
        this.candidatureRepo = candidatureRepo;
        this.offreRepo = offreRepo;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.candidatRepo= candidatRepo;
    }

    @Override
    public Candidature addCandidature(Candidature candidature, String email, Long offerId) {
        // Trouver l'offre
        Offre offre = offreRepo.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offre non trouvée avec ID : " + offerId));
        System.out.println("Offre trouvée : " + offre);  // Log de l'offre récupérée
        // 2. Trouver l'utilisateur par email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec email : " + email));

        // 3. Récupérer le candidat lié à ce user
        Candidat candidat = candidatRepo.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Candidat non trouvé pour l'utilisateur avec email : " + email));

        candidature.setCandidat(candidat);
        candidature.setOffre(offre);
        candidature.setDateSoumission(new Date());
        if (candidature.getStatus() == null) {
            candidature.setStatus(Status.EnAttente);
        } // Assuming a default status
        candidature.setOffre(offre);
        return candidatureRepo.save(candidature);
    }

    @Override
    public List<Candidature> getCandidature() {
        return candidatureRepo.findAll();
    }

    @Override
    public void deleteCandidature(Long id) {
        candidatureRepo.deleteById(id);
    }

    @Override
    public void saveCV(Long id, MultipartFile file) throws IOException {
        File uploadDirFile = new File(uploadDir);
        System.out.println("Current Working Directory: " + System.getProperty(uploadDir));
        System.out.println(uploadDirFile);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }
        String fileName = id + "_" + file.getOriginalFilename();
        Path destinationPath = Paths.get(uploadDir, fileName);
        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        Optional<Candidature> optionalCandidature = candidatureRepo.findById(id);
        if (optionalCandidature.isPresent()) {
            Candidature candidature = optionalCandidature.get();
            candidature.setCv(fileName);
            candidatureRepo.save(candidature);
        } else {
            throw new RuntimeException("Entreprise not found with id: " + id);
        }
    }

    @Override
    public void saveLettre(Long id, MultipartFile file) throws IOException {
        File uploadDirFile = new File(uploadDir);
        System.out.println("Current Working Directory: " + System.getProperty(uploadDir));
        System.out.println(uploadDirFile);
        if (file != null && !file.isEmpty()) {
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }
            String fileName = id + "_" + file.getOriginalFilename();
            Path destinationPath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            Optional<Candidature> optionalCandidature = candidatureRepo.findById(id);
            if (optionalCandidature.isPresent()) {
                Candidature candidature = optionalCandidature.get();
                candidature.setLettre_M(fileName);
                candidatureRepo.save(candidature);
            } else {
                throw new RuntimeException("Entreprise not found with id: " + id);
            }
        } else {
            // Si aucun fichier n'est fourni, vous pouvez choisir de ne pas mettre à jour le champ 'Lettre_M'
            Optional<Candidature> optionalCandidature = candidatureRepo.findById(id);
            if (optionalCandidature.isPresent()) {
                Candidature candidature = optionalCandidature.get();
                // Si vous voulez explicitement ne rien faire, vous pouvez ignorer cette partie
                // candidature.setLettre_M(null); // Si vous souhaitez supprimer la lettre de motivation, par exemple
                candidatureRepo.save(candidature);
            } else {
                throw new RuntimeException("Candidature not found with id: " + id);
            }
        }
    }
    public void updateCandidature(Long id, Candidature updatedCandidature) {
        Optional<Candidature> existingCandidatureOptional = candidatureRepo.findById(id);
        if (existingCandidatureOptional.isPresent()) {
            Candidature existingCandidature = existingCandidatureOptional.get();
            existingCandidature.setStatus(updatedCandidature.getStatus());
            candidatureRepo.save(existingCandidature);
        } else {
            System.out.println("Candidature avec l'ID " + id + " n'a pas été trouvée");
        }
    }

   /* @Override
    public List<Candidature> getCandidaturesByUserId(Long userId) {
        return candidatureRepo.findByUserId(userId);
    }*/
    @Override
    public List<Candidature> getCandidaturesByOfferId(Long offerId) {
        return candidatureRepo.findByOffre_OfferId(offerId);
    }
    @Override
    public List<Candidature> getCandidaturesByCandidatId(Long candidatId) {
        return candidatureRepo.findByCandidatId(candidatId);
    }
    @Override
    public List<Candidature> getCandidaturesByUserId(Long userId) {
        Candidat candidat = candidatRepo.findByUserId(userId);
        return candidatureRepo.findByCandidatId(candidat.getId());
    }
    @Override
    public Optional<Candidature> getCandidatureById(Long id) {
        return candidatureRepo.findById(id);
    }
    @Override
    public Candidature acceptCandidature(Long id) {
        Candidature candidature = candidatureRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature not found with id: " + id));
        candidature.setStatus(Status.Accepte); // Assuming you have an ACCEPTED status in your Status enum
        return candidatureRepo.save(candidature);
    }
    @Override
    public Candidature RefuserCandidature(Long id) {
        Candidature candidature = candidatureRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature not found with id: " + id));
        candidature.setStatus(Status.Refuse); // Assuming you have an ACCEPTED status in your Status enum
        return candidatureRepo.save(candidature);
    }
    @Override
    public void sendNotificationToEmail(String recipientEmail, String message){
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(recipientEmail);
            messageHelper.setSubject("Technical interview invitation ");
            messageHelper.setText(message);
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email to " + recipientEmail, e);
        }
    }
    @Override
    public void sendNotificationToEmailF(String recipientEmail, String message){
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(recipientEmail);
            messageHelper.setSubject("Thank You for Your Application ");
            messageHelper.setText(message);
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email to " + recipientEmail, e);
        }
    }
@Override
public void sendEmailAccept(Long id, String authorizationHeader) {
    // Vérifier le header JWT
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        throw new RuntimeException("Authorization header is missing or invalid");
    }

    // Extraire le token et l'email
    String token = authorizationHeader.substring(7);
    String email = jwtUtil.extractEmail(token);

    // Récupérer la candidature via le client Feign
    Candidature candidature = candidatureRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Candidature not found with ID: " + id));

    String titre = candidature.getOffre().getTitre();

    // Calculer la date d'entretien (ajouter 3 jours)
    LocalDate resultDate = LocalDate.now().plusDays(3);

    // Créer le message
    String message = new StringBuilder()
            .append("Dear Candidate,\n\n")
            .append("We have received your application for the position of ").append(titre).append(".\n")
            .append("We are pleased to invite you to an interview which will take place on ")
            .append(resultDate).append(" at our offices.\n")
            .append("During this interview, we will discuss your application, your skills, and your expectations in more detail.\n\n")
            .append("Best regards,\n")
            .append("The Recruitment Team")
            .toString();

    // Envoyer l'email
    sendNotificationToEmail(email, message);
}

    @Override
    public void sendEmailRefuse(Long id, String authorizationHeader) {
        // Vérifier le header JWT
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization header is missing or invalid");
        }

        // Extraire le token et l'email
        String token = authorizationHeader.substring(7);
        String email = jwtUtil.extractEmail(token);

        // Récupérer la candidature via le client Feign
        Candidature candidature = candidatureRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature not found with ID: " + id));

        String titre = candidature.getOffre().getTitre();

        // Calculer la date d'entretien (ajouter 3 jours)
        LocalDate resultDate = LocalDate.now().plusDays(3);

        // Créer le message
        String message = new StringBuilder()
                .append("Dear Candidate,\n\n")
                .append("Thank you for your application for the position of ").append(titre).append(".\n")
                .append("After careful consideration, we regret to inform you that we have decided not to move forward with your application.\n")
                .append("We appreciate your interest in joining our team and wish you all the best in your job search.\n\n")
                .append("Best regards,\n")
                .append("The Recruitment Team")
                .toString();

        // Envoyer l'email
        sendNotificationToEmailF(email, message);
    }

}
