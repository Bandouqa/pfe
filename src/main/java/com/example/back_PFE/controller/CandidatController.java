package com.example.back_PFE.controller;

import com.example.back_PFE.email.EmailServiceCandidat;
import com.example.back_PFE.entities.Candidat;
import com.example.back_PFE.entities.Offre;
import com.example.back_PFE.services.CandidatService;
import com.example.back_PFE.services.OffreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/candidat")
@CrossOrigin(origins = "http://localhost:4200")
public class CandidatController {
    @Autowired
    CandidatService candidatService ;
    @PostMapping
    public Candidat addCandidat (@RequestBody Candidat candidat) {
        return candidatService.addCandidat(candidat);
    }

    private final EmailServiceCandidat emailServiceCandidat;

    public CandidatController(EmailServiceCandidat emailServiceCandidat) {
        this.emailServiceCandidat = emailServiceCandidat;
    }

   /* @PostMapping("/checking")
    public String checkingCandidatReques(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String name = body.get("nom");
        // Logique d'acceptation du formulaire ici...
        // Envoi de l'e-mail après acceptation
        emailServiceCandidat.sendApprovalEmail(email, name);
        return "Demande acceptée et e-mail envoyé à " + email;
    } */




}
