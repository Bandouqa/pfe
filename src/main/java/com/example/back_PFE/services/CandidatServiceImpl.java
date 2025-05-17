package com.example.back_PFE.services;

import com.example.back_PFE.entities.Candidat;
import com.example.back_PFE.repository.CandidatRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CandidatServiceImpl implements CandidatService {
    @Autowired
    CandidatRepo candidatRepo;
    @Override
    public Candidat addCandidat(Candidat candidat) {
        return candidatRepo.save(candidat);
    }

}
