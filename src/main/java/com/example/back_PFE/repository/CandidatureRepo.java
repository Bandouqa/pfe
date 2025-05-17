package com.example.back_PFE.repository;

import com.example.back_PFE.entities.Candidature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidatureRepo  extends JpaRepository<Candidature, Long> {
    List<Candidature> findByOffre_OfferId( Long offerId);

    List<Candidature> findByCandidatId(Long candidatId);
}
