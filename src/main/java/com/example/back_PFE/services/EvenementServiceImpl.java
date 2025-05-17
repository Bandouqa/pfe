package com.example.back_PFE.services;

import com.example.back_PFE.entities.Evenement;
import com.example.back_PFE.repository.EvenementRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EvenementServiceImpl implements EvenementService {
    @Autowired
    EvenementRepo evenementRepo;
    @Override
    public Evenement addEvenement(Evenement evenement, String titre) {
        evenement.setTitre(evenement.getTitre());
        evenement.setHeure(evenement.getHeure());
        evenement.setAdress(evenement.getAdress());
        evenement.setLien(evenement.getLien());
        evenement.setImage(evenement.getImage());
        return evenementRepo.save(evenement);
    }
    @Override
    public Evenement updateEvenement(Evenement evenement, long id_event) {
        return evenementRepo.findById(id_event).map(old -> {
            old.setTitre(evenement.getTitre());
            old.setDate(evenement.getDate());
            old.setHeure(evenement.getHeure());
            old.setAdress(evenement.getAdress());
            old.setLien(evenement.getLien());
            old.setImage(evenement.getImage());
            return evenementRepo.save(old);
        }).orElseThrow(() -> new EntityNotFoundException("Evenement with ID " + id_event + " not found"));

    }
    @Override
    public List<Evenement> getEvenement() {
        return evenementRepo.findAll();
    }

    @Override
    public Map<String, Boolean> deleteEvenement(long id_event) {
        evenementRepo.deleteById(id_event);
        Map<String,Boolean> res = new HashMap<>();
        res.put("deleted",Boolean.TRUE);
        return res;
    }

    @Override
    public Optional<Evenement> getEvenement(long id_event) {
        return evenementRepo.findById(id_event);
    }



}

