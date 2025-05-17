package com.example.back_PFE.services;

import com.example.back_PFE.entities.Partenaire;
import com.example.back_PFE.entities.Status;
import com.example.back_PFE.jwt.JwtUtil;
import com.example.back_PFE.repository.PartenaireRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class PartenaireServiceImpl implements PartenaireService  {
@Autowired
PartenaireRepo partenaireRepo;
    @Autowired
    private JavaMailSender javaMailSender;
    private final JwtUtil jwtUtil;
    public PartenaireServiceImpl (JwtUtil jwtUtil){
        this.jwtUtil=jwtUtil;
    }
    @Override
    public Partenaire addPartenaire(Partenaire partenaire) {
        if (partenaire.getStatus() == null) {
            partenaire.setStatus(Status.EnAttente);
        }
        return partenaireRepo.save(partenaire);
    }
    @Override
    public Partenaire updatePartenaire(Partenaire partenaire, long id) {
        return partenaireRepo.findById(id).map(old -> {
            old.setNom(partenaire.getNom());
            old.setPost(partenaire.getPost());
            old.setS_nom(partenaire.getS_nom());
            old.setC_date(partenaire.getC_date());
            old.setType(partenaire.getType());
            old.setAddress(partenaire.getAddress());
            old.setPhone(partenaire.getPhone());
            old.setEmail(partenaire.getEmail());
            old.setActivite(partenaire.getActivite());
            old.setLien(partenaire.getLien());
            old.setArea(partenaire.getArea());
            old.setDescription(partenaire.getDescription());
            old.setSuggestion(partenaire.getSuggestion());
            return partenaireRepo.save(old);
        }).orElseThrow(() -> new EntityNotFoundException("Partenaire with ID " + id + " not found"));

    }
    @Override
    public List<Partenaire> getPartenaire() {
        return partenaireRepo.findAll();
    }



    @Override
    public void deletePartenaire(Long id) {
        partenaireRepo.deleteById(id);
    }


    @Override
    public Partenaire acceptPartenaire(Long id) {
        Partenaire partenaire = partenaireRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("partenaire not found with id: " + id));
        partenaire.setStatus(Status.Accepte); // Assuming you have an ACCEPTED status in your Status enum
        return partenaireRepo.save(partenaire);
    }
    @Override
    public Partenaire refuserPartenaire(Long id) {
        Partenaire partenaire = partenaireRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("partenaire not found with id: " + id));
        partenaire.setStatus(Status.Refuse); // Assuming you have an ACCEPTED status in your Status enum
        return partenaireRepo.save(partenaire);
    }
    @Override
    public Optional<Partenaire> getPartenaireById(Long id) {
        return partenaireRepo.findById(id);
    }
    @Override
    public void sendNotificationToEmail(String recipientEmail, String message){
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(recipientEmail);
            messageHelper.setSubject("Request Accepted ");
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
            messageHelper.setSubject("Thank You for Your Application");
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

        // Récupérer la partenaire via le client Feign
        Partenaire partenaire = partenaireRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("partenaire not found with ID: " + id));

        String nom = partenaire.getNom();
        String email = partenaire.getEmail();

        // Calculer la date d'entretien (ajouter 3 jours)
        LocalDate resultDate = LocalDate.now().plusDays(3);

        // Créer le message
        String message = new StringBuilder()
                .append("Hello ").append(nom).append(",\n\n")
                .append("We have received your partnership request and we are pleased to inform you that it has been accepted.\n")
                .append("We would like to invite you to a meeting that will take place on ").append(resultDate).append(".\n\n")
                .append("During this meeting, we will have the opportunity to discuss your proposal in more detail, ")
                .append("explore potential collaborations, and understand your expectations.\n\n")
                .append("Best regards,\n")
                .append("The Partnership Team")
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

        // Récupérer la partenaire via le client Feign
        Partenaire partenaire = partenaireRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("partenaire not found with ID: " + id));

        String nom = partenaire.getNom();
        String email = partenaire.getEmail();

        // Créer le message
        String message = new StringBuilder()
                .append("Hello ").append(nom).append(",\n\n")
                .append("Thank you for your interest in partnering with us.\n")
                .append("After careful consideration, we regret to inform you that we will not be moving forward with your partnership request at this time.\n\n")
                .append("We appreciate your proposal and encourage you to stay in touch for future opportunities.\n\n")
                .append("Best regards,\n")
                .append("The Partnership Team")
                .toString();

        // Envoyer l'email
        sendNotificationToEmailF(email, message);
    }
}

