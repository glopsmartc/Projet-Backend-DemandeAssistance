package com.demandeAssistance.demandeAssistance.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreationDossierDTO {
    private String description; // Description de la demande
    private String type; // Type de la demande
    private Long idContrat; // Identifiant du contrat
    private Boolean maladieChronique;
    private String descriptionMaladie;
    private String positionActuelle;

    private String priorite;
}
