package com.example.back_PFE.controller;

import com.example.back_PFE.email.EmailService;
import com.example.back_PFE.email.EmailServiceP;
import com.example.back_PFE.entities.Candidature;
import com.example.back_PFE.entities.Client;
import com.example.back_PFE.entities.Offre;
import com.example.back_PFE.entities.Partenaire;
import com.example.back_PFE.services.PartenaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/partenaire")
public class PartenaireController {
    private final EmailServiceP emailServiceP;

    public PartenaireController (EmailServiceP emailServiceP){
        this.emailServiceP = emailServiceP;
    }
    /*@PostMapping("/soumi")
    public String soummissionPartenaireRequest(@RequestBody Map<String, String> body){
        String email = body.get("email");
        String name = body.get("nom");

        emailServiceP.sendApprovalEmail(email, name);
        return "e-mail envoyé à " + email;
    }*/
    @Autowired
    PartenaireService partenaireService;
    @PostMapping
    public Partenaire addPartenaire(@RequestBody Partenaire partenaire) {
        // Enregistrement du partenaire dans la base de données
        Partenaire savedPartenaire = partenaireService.addPartenaire(partenaire);

        // Envoi de l'email de confirmation après enregistrement
        emailServiceP.sendApprovalEmail(savedPartenaire.getEmail(), savedPartenaire.getNom());

        return savedPartenaire;
    }
    @PutMapping (value = "/{id}")
    public Partenaire updatePartenaire(@RequestBody Partenaire partenaire , @PathVariable ("id") long id){
        return partenaireService.updatePartenaire(partenaire,id);
    }
    @GetMapping
    public List<Partenaire> getAll()
    {
        return partenaireService.getPartenaire();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePartenaire(@PathVariable("id") Long id) {
        partenaireService.deletePartenaire(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/accept/{id}")
    public ResponseEntity<?> acceptPartenaire(@PathVariable Long id , @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Accept the candidature (e.g., update status)
            Partenaire updatedPartenaire = partenaireService.acceptPartenaire(id);

            // Send the acceptance email
            partenaireService.sendEmailAccept(id, authorizationHeader);

            // Return the updated candidature in the response
            return ResponseEntity.ok(updatedPartenaire);
        } catch (RuntimeException e) {
            // Return an error response in case of exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    @PostMapping("/Refuse/{id}")
    public ResponseEntity<?> refusePartenaire(@PathVariable Long id,@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Accept the candidature (e.g., update status)
            Partenaire updatedPartenaire = partenaireService.refuserPartenaire(id);

            // Send the acceptance email
            partenaireService.sendEmailRefuse(id, authorizationHeader);

            // Return the updated candidature in the response
            return ResponseEntity.ok(updatedPartenaire);
        } catch (RuntimeException e) {
            // Return an error response in case of exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Partenaire> getPartenaireById(@PathVariable("id") Long id) {
        Optional<Partenaire> partenaire = partenaireService.getPartenaireById(id);
        return partenaire.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
