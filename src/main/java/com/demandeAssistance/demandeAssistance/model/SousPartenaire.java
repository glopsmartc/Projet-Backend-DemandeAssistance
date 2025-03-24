package com.demandeAssistance.demandeAssistance.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "sous_partenaire", schema = "demande_assistance") // Table et schéma
@NoArgsConstructor
@Getter
@Setter
public class SousPartenaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSousPartenaire;

    private String nomEntreprise;

    private String prenom;

    private String nom;

    @NotNull(message = "Le numéro de téléphone est obligatoire")
    @Column(name = "num_tel", nullable = false)
    private String numTel;

    @NotBlank(message = "La zone géographique est obligatoire")
    private String zoneGeographique;

    private String adresse;

    @Column(columnDefinition = "TEXT")
    private String servicesProposes; // Liste sous forme de chaîne (exp: "service1,service2,service3")

    @ManyToMany(mappedBy = "sousPartenaires")
    @JsonBackReference  // Ignorer cette relation pendant la sérialisation
    private List<DossierAssistance> dossiersAssistance;
}
