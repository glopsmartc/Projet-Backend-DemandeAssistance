package com.demandeAssistance.demandeAssistance.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "dossier_assistance", schema = "demande_assistance") // Table et schéma
@NoArgsConstructor
@Getter
@Setter
public class DossierAssistance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDossier;

    private String typeAssistance;

    @Column(columnDefinition = "TEXT") // Permet de stocker un texte long
    private String description; // Texte pouvant contenir des images ou des fichiers (gestion via front ou service)

    private LocalDate dateOuverture;

    private LocalDate dateCloture;

    private String statutDossier;

    @ElementCollection // Permet de gérer une liste simple (String ici)
    @CollectionTable(
            name = "dossier_actions", // Nom de la table pour la liste
            joinColumns = @JoinColumn(name = "dossier_id"),// Clé étrangère liant à DossierAssistance
            schema = "demande_assistance"
    )
    @Column(name = "action_realisee") // Nom de la colonne dans la table des actions
    private List<String> actionsRealisees;

    private Double fraisTotalDepense;

    @ElementCollection
    @CollectionTable(
            name = "dossier_factures", // Nom de la table pour la liste des factures
            joinColumns = @JoinColumn(name = "dossier_id"),
            schema = "demande_assistance"
    )
    @Column(name = "factures") // nom de colonne dans la table des factures
    private List<String> factures; // Liste des chemins ou URLs des PDF des factures

    @ElementCollection
    @CollectionTable(
            name = "dossier_documents", // Nom de la table pour la liste des documents
            joinColumns = @JoinColumn(name = "dossier_documents"),
            schema = "demande_assistance"
    )
    @Column(name = "documents") // nom de colonne dans la table des documents
    private List<String> documents; // Liste des chemins ou URLs des PDF des documents

    // l'identifiant du contrat (référence au microservice Contrat)
    private Long idContrat;

    private String positionActuelle;

    private String priorite;

    @NotNull(message = "Le numéro de téléphone est obligatoire")
    //@Pattern(regexp = "\\+?[0-9]{10,15}", message = "Le numéro de téléphone est invalide")
    @Column(name = "num_tel", nullable = false)
    private String numTel;

    @NotNull(message = "L'email est obligatoire")
    @Email(message = "Le format de l'email est invalide")
    @Column(name = "email", nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn(name = "sous_partenaire_id", nullable = false)
    private SousPartenaire sousPartenaire;
}

