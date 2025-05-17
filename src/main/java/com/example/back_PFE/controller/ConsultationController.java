package com.example.back_PFE.controller;

import com.example.back_PFE.email.EmailServiceConsultation;
import com.example.back_PFE.email.EmailServiceP;
import com.example.back_PFE.entities.Client;
import com.example.back_PFE.entities.Consultation;
import com.example.back_PFE.entities.Partenaire;
import com.example.back_PFE.services.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/Consultation")
@CrossOrigin(origins = "http://localhost:4200")
public class ConsultationController {
    private final EmailServiceConsultation emailServiceConsultation;

    public ConsultationController (EmailServiceConsultation emailServiceConsultation){
        this.emailServiceConsultation = emailServiceConsultation;
    }
    @PostMapping("/confirmation")
    public String confirmationRendezvous(@RequestBody Map<String, String> body){
        String email = body.get("email");
        String name = body.get("nom");

        emailServiceConsultation.sendApprovalEmail(email, name);
        return "e-mail envoyé à " + email;
    }

    @Autowired
    private ConsultationService consultationService;

    @PostMapping("/consulter")
    public Consultation consulter(@RequestBody Consultation consultation) {
        Consultation savedConsult= consultationService.consulter(consultation);

        emailServiceConsultation.sendApprovalEmail(savedConsult.getEmail(), savedConsult.getNom());

        return savedConsult;
    }

    @GetMapping("/heures-dispo/{date}")
    public ResponseEntity<List<LocalTime>> getHeuresDispo(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            return ResponseEntity.ok(consultationService.getHeuresDispo(date));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @GetMapping
    public List<Consultation> getAll()
    {
        return consultationService.getConsultation();
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteConsult(@PathVariable("id") Long id) {
        consultationService.deleteConsult(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/accept/{id}")
    public ResponseEntity<?> acceptConsult(@PathVariable Long id , @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Accept the candidature (e.g., update status)
            Consultation updatedConsult = consultationService.acceptConsult(id);

            // Send the acceptance email
            consultationService.sendEmailAccept(id, authorizationHeader);

            // Return the updated candidature in the response
            return ResponseEntity.ok(updatedConsult);
        } catch (RuntimeException e) {
            // Return an error response in case of exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    @PostMapping("/Refuse/{id}")
    public ResponseEntity<?> refuseconsult(@PathVariable Long id,@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Accept the candidature (e.g., update status)
            Consultation updatedConsult = consultationService.refuseconsult(id);

            // Send the acceptance email
            consultationService.sendEmailRefuse(id, authorizationHeader);

            // Return the updated candidature in the response
            return ResponseEntity.ok(updatedConsult);
        } catch (RuntimeException e) {
            // Return an error response in case of exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Consultation> getConsultationById(@PathVariable("id") Long id) {
        Optional<Consultation> consultation = consultationService.getConsultationById(id);
        return consultation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
