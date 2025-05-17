package com.example.back_PFE.entities;


import com.example.back_PFE.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Candidat")
@ToString(exclude = "user")
public class Candidat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nom;
    private String prenom;
    private String email;
    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

}
