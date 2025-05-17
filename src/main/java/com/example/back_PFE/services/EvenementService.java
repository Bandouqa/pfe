package com.example.back_PFE.services;

import com.example.back_PFE.entities.Evenement;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EvenementService {

    public Evenement updateEvenement(Evenement evenement, long id_event);
    public List<Evenement> getEvenement();
    public Map<String,Boolean> deleteEvenement(long id_event);
    public Optional<Evenement> getEvenement(long id_event);


    Evenement addEvenement(Evenement evenement, String email);
}
