package com.example.back_PFE.services;

import com.example.back_PFE.entities.Offre;
import com.example.back_PFE.repository.OffreRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class OffreServiceImpl  implements OffreService {
    @Autowired
    OffreRepo offreRepo;
    @Override
    public Offre addOffreEmploi(Offre offre) {
        offre.setTitre(offre.getTitre());
        offre.setDescription(offre.getDescription());
        offre.setProfil(offre.getProfil()) ;
        offre.setExigence(offre.getExigence());
        offre.setLocalisation(offre.getLocalisation()); ;
        offre.setAventage(offre.getAventage());
        offre.setDatePost(offre.getDatePost());
        offre.setDateFin(offre.getDateFin());
        offre.setPre_entreprise(offre.getPre_entreprise());
        return offreRepo.save(offre);
    }
    @Override
    public Offre updateOffre(Offre offre, long offerId) {
        return offreRepo.findById(offerId).map(old -> {
            old.setTitre(offre.getTitre());
            old.setDescription(offre.getDescription());
            old.setLocalisation(offre.getLocalisation());
            old.setProfil(offre.getProfil());
            old.setExigence(offre.getExigence());
            old.setAventage(offre.getAventage());
            old.setDatePost(offre.getDatePost());
            old.setDateFin(offre.getDateFin());
            old.setPre_entreprise(offre.getPre_entreprise());
            return offreRepo.save(old);
        }).orElseThrow(() -> new EntityNotFoundException("Offre with ID " + offerId + " not found"));

    }
    @Override
    public List<Offre> getAll() {
        return offreRepo.findAll();
    }

    @Override
    public Map<String, Boolean> deleteOffre(long offerId) {
        offreRepo.deleteById(offerId);
        Map<String,Boolean> res = new HashMap<>();
        res.put("deleted",Boolean.TRUE);
        return res;
    }

    @Override
    public Optional<Offre> getOffre(long offerId) {
        return offreRepo.findById(offerId);
    }
    @Override
    public List<Offre> searchOffresByTitre(String keyword) {
        return offreRepo.findByTitreContainingIgnoreCase(keyword);
    }

    @Override
    public Map<String, Boolean> deleteOffreEmploi(Long offerId) {
        offreRepo.deleteById(offerId);
        Map<String,Boolean> res = new HashMap<>();
        res.put("deleted",Boolean.TRUE);
        return res;
    }
    @Override
    public List<Offre> getOffresValides() {
        Date currentDate = new Date();
        return offreRepo.findAll()
                .stream()
                .filter(offre -> offre.getDateFin() != null && offre.getDateFin().after(currentDate))
                .collect(Collectors.toList());
    }
}



