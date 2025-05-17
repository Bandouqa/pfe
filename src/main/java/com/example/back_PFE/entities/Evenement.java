package com.example.back_PFE.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Evenement")
public class Evenement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_event;
    private String titre;
    private LocalDate date;
    private LocalTime heure;
    private String adress;
    private String lien;
    private String image;
}
