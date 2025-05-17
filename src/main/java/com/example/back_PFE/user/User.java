package com.example.back_PFE.user;

import com.example.back_PFE.entities.Candidat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "app_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "candidat")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String nom;
    @Column(unique = true)
    private String prenom;
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToOne(mappedBy = "user" , cascade = CascadeType.ALL)
    @JsonIgnore
    private Candidat candidat;
}
