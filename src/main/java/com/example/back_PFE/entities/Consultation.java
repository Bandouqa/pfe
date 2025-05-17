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
@Table(name ="Consultation")
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nom;
    private String email;
    private String phone;
    private String education;
    private LocalDate date;
    private LocalTime heure;
    private String type ;
    private String residence;
    @Enumerated(EnumType.STRING)
    private Status status;
}
