package com.example.back_PFE.repository;

import com.example.back_PFE.entities.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvenementRepo extends JpaRepository<Evenement, Long> {
}
