package com.example.back_PFE.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name ="Offre")
public class Offre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private long  offerId;
    private String titre;
    private String description;
    private String profil;
    private String exigence;
    private String localisation;
    private String aventage;
    private String pre_entreprise;
    private Date datePost;
    private Date dateFin;
    @OneToMany(cascade = {CascadeType.REMOVE},mappedBy = "offre")
    @JsonIgnore
    @ToString.Exclude
    private List<Candidature> candidatures;
}
