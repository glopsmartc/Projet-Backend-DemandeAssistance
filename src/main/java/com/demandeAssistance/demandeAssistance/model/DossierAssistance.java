package com.demandeAssistance.demandeAssistance.model;
import jakarta.persistence.*;
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

    @Column(columnDefinition = "TEXT") // Permet de stocker un texte long
    private String description; // Texte pouvant contenir des images ou des fichiers (gestion via front ou service)

    private LocalDate dateOuverture;

    private LocalDate dateCloture;

    private String statutDossier;

    @ElementCollection // Permet de gérer une liste simple (String ici)
    @CollectionTable(
            name = "dossier_actions", // Nom de la table pour la liste
            joinColumns = @JoinColumn(name = "dossier_id") // Clé étrangère liant à DossierAssistance
    )
    @Column(name = "action_realisee") // Nom de la colonne dans la table des actions
    private List<String> actionsRealisees;

    private Double fraisTotalDepense;

    private String constat; // Stocke le chemin ou l'URL vers le PDF du constat

    @ElementCollection
    @CollectionTable(
            name = "dossier_factures", // Nom de la table pour la liste des factures
            joinColumns = @JoinColumn(name = "dossier_id")
    )
    @Column(name = "factures") // nom de colonne dans la table des factures
    private List<String> factures; // Liste des chemins ou URLs des PDF des factures
}

