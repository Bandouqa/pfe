package com.example.back_PFE.entities;

import com.example.back_PFE.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_client;
    private String nom;
    private String prenom;
    private String email;
    private String phone;
    private String specialite;
    private String niveau;
    private String adress;
    @Enumerated(EnumType.STRING)
    private Statusdoss status = Statusdoss.Depose;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
