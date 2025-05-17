package com.example.back_PFE.repository;

import com.example.back_PFE.entities.Partenaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartenaireRepo extends JpaRepository<Partenaire, Long> {
}
