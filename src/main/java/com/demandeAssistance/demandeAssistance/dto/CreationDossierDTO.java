package com.demandeAssistance.demandeAssistance.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class CreationDossierDTO {
    private String description; // Description de la demande
    private Long idContrat; // Identifiant du contrat
}
