package com.example.back_PFE.repository;

import com.example.back_PFE.entities.Statusdoss;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter

public class ClientFileDTO {
    private Long Id_client;
    private String nom;
    private String prenom;
    private String email;
    private String phone;
    private String specialite;
    private String niveau;
    private String adress;
    private List<String> fichiers;
    private Long userId;
    private Statusdoss status;
}
