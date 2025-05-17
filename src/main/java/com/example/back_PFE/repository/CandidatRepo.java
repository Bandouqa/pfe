package com.example.back_PFE.repository;

import com.example.back_PFE.entities.Candidat;
import com.example.back_PFE.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidatRepo extends JpaRepository<Candidat, Long> {
    Optional<Candidat> findByUser(User user);

    Candidat findByUserId(Long userId);

}
