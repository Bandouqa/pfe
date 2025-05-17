package com.example.back_PFE.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Partenaire")
public class Partenaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nom;
    private String post;
    private String s_nom;
    private LocalDate c_date;
    private String type;
    private String address;
    private String phone;
    private String email;
    private String activite;
    private String lien;
    private String area;
    private String description;
    private String suggestion;
    @Enumerated(EnumType.STRING)
    private Status status;

}
