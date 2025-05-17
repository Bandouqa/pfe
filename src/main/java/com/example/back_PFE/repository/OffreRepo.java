package com.example.back_PFE.repository;

import com.example.back_PFE.entities.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OffreRepo extends JpaRepository<Offre, Long> {
    List<Offre> findByTitreContainingIgnoreCase(String keyword);
}
