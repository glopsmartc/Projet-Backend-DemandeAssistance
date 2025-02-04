package com.demandeAssistance.demandeAssistance.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MaladieChroniqueDTO {

    Boolean maladieChronique;
    String descriptionMaladie;
    private String email;

    public MaladieChroniqueDTO(Boolean maladieChronique, String descriptionMaladie, String email) {
        this.maladieChronique = maladieChronique;
        this.descriptionMaladie = descriptionMaladie;
        this.email = email;
    }
}
