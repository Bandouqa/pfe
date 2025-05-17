package com.example.back_PFE.services;

import com.example.back_PFE.entities.Offre;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OffreService {
    public Offre updateOffre(Offre offre,long offerId);
    public List<Offre> getAll();
    public Map<String,Boolean> deleteOffre(long offerId);
    public Optional<Offre> getOffre(long offerId);

    Offre addOffreEmploi(Offre offre);
    List<Offre> searchOffresByTitre(String keyword);

    Map<String, Boolean> deleteOffreEmploi(Long offreId);

    List<Offre> getOffresValides();
}
