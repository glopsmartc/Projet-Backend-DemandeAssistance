package com.demandeAssistance.demandeAssistance.service;

import com.demandeAssistance.demandeAssistance.model.DossierAssistance;
import com.demandeAssistance.demandeAssistance.model.SousPartenaire;

import java.util.List;
import java.util.Optional;

public interface SousPartenaireServiceInterface {
    SousPartenaire ajouterSousPartenaire(SousPartenaire sousPartenaire);

    List<SousPartenaire> obtenirTousLesSousPartenaires();

    Optional<SousPartenaire> obtenirSousPartenaireParId(Long id);

    SousPartenaire modifierSousPartenaire(Long id, SousPartenaire sousPartenaireModifie);

    void supprimerSousPartenaire(Long id);

    DossierAssistance assignerSousPartenaireDossier(Long idSousPartenaire, Long idDossier);
}
