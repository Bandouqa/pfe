package com.example.back_PFE.services;

import com.example.back_PFE.entities.Consultation;
import com.example.back_PFE.entities.Status;
import com.example.back_PFE.jwt.JwtUtil;
import com.example.back_PFE.repository.ConsultationRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class ConsultationServiceImpl implements ConsultationService {
    @Autowired
    private ConsultationRepo consultationRepo;
    @Autowired
    private JavaMailSender javaMailSender;
    private final JwtUtil jwtUtil;
    public ConsultationServiceImpl(JwtUtil jwtUtil){
        this.jwtUtil=jwtUtil;
    }

    public Consultation consulter(Consultation consult) {
        // Vérifier si la date choisie est un jeudi
        if (!consult.getDate().getDayOfWeek().equals(DayOfWeek.THURSDAY)) {
            throw new IllegalArgumentException("Les consultations ont lieu uniquement le jeudi !");
        }

        // Vérifier si l'heure est déjà réservée ce jour-là
        boolean heureDejaPrise = consultationRepo.existsByDateAndHeure(consult.getDate(), consult.getHeure());
        if (heureDejaPrise) {
            throw new IllegalArgumentException("L'heure choisie est déjà réservée !");
        }
        // Définir un status par défaut si null
        if (consult.getStatus() == null) {
            consult.setStatus(Status.EnAttente);
        }
        return consultationRepo.save(consult);
    }


    // Retourner les heures disponibles pour un jeudi donné
    public List<LocalTime> getHeuresDispo(LocalDate date) {
        if (!date.getDayOfWeek().equals(DayOfWeek.THURSDAY)) {
            throw new IllegalArgumentException("Veuillez choisir un jeudi.");
        }
        // Générer les créneaux de 30 minutes entre 09:00 et 17:00
        List<LocalTime> heuresDispo = new ArrayList<>();
        LocalTime debut = LocalTime.of(10, 0);  // Heure de début
        LocalTime fin = LocalTime.of(19, 30);   // Heure de fin

        while (debut.isBefore(fin)) {
            heuresDispo.add(debut);
            debut = debut.plusMinutes(30); // Ajouter 30 minutes
        }
        List<LocalTime> heuresRéservées = consultationRepo.findByDate(date)
                .stream().map(Consultation::getHeure).toList();

        return heuresDispo.stream()
                .filter(h -> !heuresRéservées.contains(h))
                .toList();
    }
    @Override
    public List<Consultation> getConsultation() {
        return consultationRepo.findAll();
    }

    @Override
    public void deleteConsult(Long id) {
        consultationRepo.deleteById(id);
    }

    @Override
    public Optional<Consultation> getConsultationById(Long id) {
        return consultationRepo.findById(id);
    }
    @Override
    public Consultation acceptConsult(Long id) {
        Consultation consultation = consultationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature not found with id: " + id));
        consultation.setStatus(Status.Accepte); // Assuming you have an ACCEPTED status in your Status enum
        return consultationRepo.save(consultation);
    }
    @Override
    public Consultation refuseconsult(Long id) {
        Consultation consultation = consultationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature not found with id: " + id));
        consultation.setStatus(Status.Refuse); // Assuming you have an ACCEPTED status in your Status enum
        return consultationRepo.save(consultation);
    }
    @Override
    public void sendNotificationToEmail(String recipientEmail, String message){
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(recipientEmail);
            messageHelper.setSubject("Session Reserved ");
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

        // Récupérer la candidature via le client Feign
        Consultation consultation = consultationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature not found with ID: " + id));

        String nom = consultation.getNom();
        String email = consultation.getEmail();



        // Créer le message
        String message = new StringBuilder()
                .append("Hello ").append(nom).append(",\n\n")
                .append("We have received your request for a coaching consultation, and we are pleased to inform you that it has been accepted.\n")
                .append("Your coaching session is scheduled to take place on [insert date] at our office.\n\n")
                .append("During this session, we will discuss your goals, expectations, and explore how we can best support your development.\n\n")
                .append("Best regards,\n")
                .append("The Coaching Team")
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

        // Récupérer la candidature via le client Feign
        Consultation consultation = consultationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature not found with ID: " + id));

        String nom = consultation.getNom();
        String email = consultation.getEmail();


        // Créer le message
        String message = new StringBuilder()
                .append("Hello ").append(nom).append(",\n\n")
                .append("Thank you for your interest in our coaching consultation.\n")
                .append("After careful consideration, we regret to inform you that we will not be moving forward with your request at this time.\n\n")
                .append("We encourage you to stay connected and consider future opportunities with us.\n\n")
                .append("Best regards,\n")
                .append("The Coaching Team")
                .toString();

        // Envoyer l'email
        sendNotificationToEmailF(email, message);
    }


}
