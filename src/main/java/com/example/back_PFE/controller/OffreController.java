package com.example.back_PFE.controller;

import com.example.back_PFE.entities.Offre;
import com.example.back_PFE.jwt.JwtUtil;
import com.example.back_PFE.services.OffreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/Offre")
@CrossOrigin(origins = "http://localhost:4200")

public class OffreController {
    private final JwtUtil jwtUtil;
    private final OffreService offreService;

    public OffreController(JwtUtil jwtUtil,OffreService offreService){
        this.offreService = offreService;
        this.jwtUtil=jwtUtil;
    }

    @PostMapping("/addoffre")
    public ResponseEntity<?> addOffreEmploi(@RequestBody Offre offre,@RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            // Extraire le token JWT et obtenir l'email
            String token = authorizationHeader.substring(7);
            String email = jwtUtil.extractEmail(token);

            System.out.println("Email extrait : " + email);

            Offre newOffreEmploi = offreService.addOffreEmploi(offre);

            return new ResponseEntity<>(newOffreEmploi, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>( e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping (value = "/{offerId}")
    public Offre updateOffre(@RequestBody Offre offre ,@PathVariable ("offerId") long offerId){
        return offreService .updateOffre(offre,offerId);
    }
    @GetMapping("/all")
    public List<Offre> getAll()
    {
        return offreService.getAll();
    }

    @DeleteMapping(value = "/{offerId}")
    public Map<String,Boolean> deleteOffre(@PathVariable("offerId")long offerId){
        return offreService.deleteOffre(offerId);}

    @GetMapping (value = "/{offerId}")
    public Optional<Offre> getOffre(@PathVariable("offerId") long offerId)
    {
        return offreService.getOffre(offerId);
    }

    @GetMapping("/search")
    public List<Offre> searchOffres(@RequestParam("keyword") String keyword) {
        return offreService.searchOffresByTitre(keyword);
    }
    @DeleteMapping("/delete/{offerId}")
    public ResponseEntity<Map<String, Boolean>> deleteOffreEmploi(@PathVariable Long offerId) {
        Map<String, Boolean> response = offreService.deleteOffreEmploi(offerId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping
    public List<Offre> getOffresValides() {
        return offreService.getOffresValides();
    }

}
