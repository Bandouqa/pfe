package com.example.back_PFE.user;

import com.example.back_PFE.entities.Candidat;
import com.example.back_PFE.repository.CandidatRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CandidatRepo candidatRepo;

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        //  Créer un candidat si le rôle est CANDIDAT
        if (user.getRole() == Role.CANDIDAT) {
            Candidat candidat = new Candidat();
            candidat.setUser(savedUser); // Assure-toi que l'entité Candidat a bien un champ `User`
            candidat.setEmail(savedUser.getEmail());

            candidatRepo.save(candidat);

        }
        return savedUser;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
