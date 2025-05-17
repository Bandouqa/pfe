package com.example.back_PFE.controller;

import com.example.back_PFE.entities.Evenement;
import com.example.back_PFE.entities.Offre;
import com.example.back_PFE.jwt.JwtUtil;
import com.example.back_PFE.services.EvenementService;
import com.example.back_PFE.services.OffreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/Evenement")
@CrossOrigin(origins = "http://localhost:4200")

public class EvenementController {
    private final JwtUtil jwtUtil;
    private final EvenementService evenementService;

    public EvenementController(JwtUtil jwtUtil,EvenementService evenementService){
        this.evenementService = evenementService;
        this.jwtUtil=jwtUtil;
    }
    @PostMapping("/add")
    public ResponseEntity<?> addEvenement(@RequestBody Evenement evenement, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            // Extraire le token JWT et obtenir l'email
            String token = authorizationHeader.substring(7);
            String email = jwtUtil.extractEmail(token);

            System.out.println("Email extrait : " + email);

            Evenement newEvenement = evenementService.addEvenement(evenement,email);

            return new ResponseEntity<>(newEvenement, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>( e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping(value = "/{id_event}")
    public Evenement updateEvenement(@RequestBody Evenement evenement, @PathVariable("id_event") long id_event) {
        return evenementService.updateEvenement(evenement, id_event);
    }

    @GetMapping
    public List<Evenement> getAll() {
        return evenementService.getEvenement();
    }

    @DeleteMapping(value = "/{id_event}")
    public Map<String, Boolean> deleteEvenement(@PathVariable("id_event") long id_event) {
        return evenementService.deleteEvenement(id_event);
    }
    @GetMapping (value = "/{id_event}")
    public Optional<Evenement> getEvenement(@PathVariable("id_event") long id_event)
    {
        return evenementService.getEvenement(id_event);
    }
}