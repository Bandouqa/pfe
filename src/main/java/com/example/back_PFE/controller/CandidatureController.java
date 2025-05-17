package com.example.back_PFE.controller;

import com.example.back_PFE.email.EmailServiceCandidat;
import com.example.back_PFE.entities.Candidature;
import com.example.back_PFE.jwt.JwtUtil;
import com.example.back_PFE.services.CandidatureService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/candidature")
@CrossOrigin(origins = "http://localhost:4200")
public class CandidatureController {
    private final CandidatureService candidatureService;
    private final JwtUtil jwtUtil;
    private final EmailServiceCandidat emailServiceCandidat;





    public CandidatureController(CandidatureService candidatureService, JwtUtil jwtUtil ,EmailServiceCandidat emailServiceCandidat) {
        this.candidatureService = candidatureService;
        this.jwtUtil = jwtUtil;
        this.emailServiceCandidat = emailServiceCandidat;

    }
    @PostMapping("/checking")
    public String checkingCandidatReques(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        // Logique d'acceptation du formulaire ici...
        // Envoi de l'e-mail après acceptation
        emailServiceCandidat.sendApprovalEmail(email);
        return "Demande acceptée et e-mail envoyé à " + email;
    }

    @PostMapping("/add/{offerId}")
    public ResponseEntity<Candidature> createCandidature(
            @RequestBody Candidature candidature,
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("offerId") Long offerId) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Extraire le token JWT et obtenir l'email
        String token = authorizationHeader.substring(7);
        String email = jwtUtil.extractEmail(token);

        System.out.println("Email extrait : " + email); // Debug

        Candidature createdCandidature = candidatureService.addCandidature(candidature, email, offerId);
        emailServiceCandidat.sendApprovalEmail(email);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCandidature);
    }
    @GetMapping("/getCandidatures")
    public ResponseEntity<List<Candidature>> getAllCandidatures(@RequestHeader("Authorization") String authorizationHeader) {
        // Vérifier si l'en-tête d'autorisation est présent et valide
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Récupérer les candidatures
        List<Candidature> candidatures = candidatureService.getCandidature();

        // Vérifier si la liste est vide
        if (candidatures.isEmpty()) {
            return ResponseEntity.noContent().build(); // Code 204 : Pas de contenu
        }


        return ResponseEntity.ok(candidatures); // Code 200 : OK
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCandidature(@PathVariable("id") Long id) {
        candidatureService.deleteCandidature(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/uploadCV/{id}")
    public ResponseEntity<String> uploadCV(@PathVariable("id") Long id, @RequestParam("file") MultipartFile file) {
        try {
            candidatureService.saveCV(id, file);
            return ResponseEntity.ok("CV uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload logo");
        }
    }
    @PostMapping("/uploadLettre/{id}")
    public ResponseEntity<String> uploadLettre(@PathVariable("id") Long id, @RequestParam(value="file" , required=false) MultipartFile file) {
        try {
            candidatureService.saveLettre(id, file);
            return ResponseEntity.ok("Lettre uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload logo");
        }
    }
    private final String uploadDir = "C:/Users/neder/Desktop/cv/uploads/";
    @GetMapping("/uploads/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        System.out.println(filename);
        File file = new File(uploadDir + filename);
        System.out.println(file.getAbsolutePath());
        if (!file.exists()) {
            System.out.println(" Fichier introuvable !");
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    @PostMapping("/accept/{id}")
    public ResponseEntity<?> acceptCandidature(@PathVariable Long id ,@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Accept the candidature (e.g., update status)
            Candidature updatedCandidature = candidatureService.acceptCandidature(id);

            // Send the acceptance email
            candidatureService.sendEmailAccept(id, authorizationHeader);

            // Return the updated candidature in the response
            return ResponseEntity.ok(updatedCandidature);
        } catch (RuntimeException e) {
            // Return an error response in case of exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    @PostMapping("/Refuse/{id}")
    public ResponseEntity<?> RefuseCandidature(@PathVariable Long id,@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Accept the candidature (e.g., update status)
            Candidature updatedCandidature = candidatureService.RefuserCandidature(id);

            // Send the acceptance email
            candidatureService.sendEmailRefuse(id, authorizationHeader);

            // Return the updated candidature in the response
            return ResponseEntity.ok(updatedCandidature);
        } catch (RuntimeException e) {
            // Return an error response in case of exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    @PutMapping("/update/{id}")
    public HttpStatus updateCandidature(@PathVariable("id") Long id, @RequestBody Candidature updatedCandidature) {
        // Récupérer la candidature mise à jour
        candidatureService.updateCandidature(id, updatedCandidature);

        return HttpStatus.OK;
    }
    @GetMapping("/by-candidat/{candidatId}")
    public ResponseEntity<List<Candidature>> getCandidaturesByCandidatId(@PathVariable Long candidatId) {
        List<Candidature> candidatures = candidatureService.getCandidaturesByCandidatId(candidatId);
        if (candidatures.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(candidatures);
    }
    @GetMapping("/by-user/{userId}")
    public List<Candidature> getCandidaturesByUserId(@PathVariable Long userId) {
        return candidatureService.getCandidaturesByUserId(userId);
    }

    @GetMapping("/offre/{offerId}")
    public ResponseEntity<List<Candidature>> getCandidaturesByOfferId(@PathVariable("offerId") Long offerId) {
        List<Candidature> candidatures = candidatureService.getCandidaturesByOfferId(offerId);
        return ResponseEntity.ok(candidatures);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Candidature> getCandidatureById(@PathVariable("id") Long id) {
        Optional<Candidature> candidature = candidatureService.getCandidatureById(id);
        return candidature.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
