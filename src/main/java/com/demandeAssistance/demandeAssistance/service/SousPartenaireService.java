package com.demandeAssistance.demandeAssistance.service;

import com.demandeAssistance.demandeAssistance.exception.DossierAssistanceNotFoundException;
import com.demandeAssistance.demandeAssistance.exception.SousPartenaireNotFoundException;
import com.demandeAssistance.demandeAssistance.model.DossierAssistance;
import com.demandeAssistance.demandeAssistance.model.DossierAssistanceRepository;
import com.demandeAssistance.demandeAssistance.model.SousPartenaire;
import com.demandeAssistance.demandeAssistance.model.SousPartenaireRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SousPartenaireService implements SousPartenaireServiceInterface {
    private final SousPartenaireRepository sousPartenaireRepository;
    private final DossierAssistanceRepository dossierAssistanceRepository;

    public SousPartenaireService(SousPartenaireRepository sousPartenaireRepository, DossierAssistanceRepository dossierAssistanceRepository) {
        this.sousPartenaireRepository = sousPartenaireRepository;
        this.dossierAssistanceRepository = dossierAssistanceRepository;
    }

    @Override
    public SousPartenaire ajouterSousPartenaire(SousPartenaire sousPartenaire) {
        return sousPartenaireRepository.save(sousPartenaire);
    }

    @Override
    public List<SousPartenaire> obtenirTousLesSousPartenaires() {
        return sousPartenaireRepository.findAll();
    }

    @Override
    public Optional<SousPartenaire> obtenirSousPartenaireParId(Long id) {
        return Optional.ofNullable(sousPartenaireRepository.findById(id)
                .orElseThrow(() -> new SousPartenaireNotFoundException("Sous-Partenaire avec ID " + id + " non trouvé.")));
    }

    @Override
    public SousPartenaire modifierSousPartenaire(Long id, SousPartenaire sousPartenaireModifie) {
        SousPartenaire sousPartenaire = sousPartenaireRepository.findById(id)
                .orElseThrow(() -> new SousPartenaireNotFoundException("Sous-Partenaire avec ID " + id + " non trouvé."));

        sousPartenaire.setNomEntreprise(sousPartenaireModifie.getNomEntreprise());
        sousPartenaire.setNom(sousPartenaireModifie.getNom());
        sousPartenaire.setPrenom(sousPartenaireModifie.getPrenom());
        sousPartenaire.setNumTel(sousPartenaireModifie.getNumTel());
        sousPartenaire.setZoneGeographique(sousPartenaireModifie.getZoneGeographique());
        sousPartenaire.setAdresse(sousPartenaireModifie.getAdresse());
        sousPartenaire.setServicesProposes(sousPartenaireModifie.getServicesProposes());

        return sousPartenaireRepository.save(sousPartenaire);
    }

    @Override
    public void supprimerSousPartenaire(Long id) {
        if (!sousPartenaireRepository.existsById(id)) {
            throw new SousPartenaireNotFoundException("Sous-Partenaire avec ID " + id + " non trouvé.");
        }
        sousPartenaireRepository.deleteById(id);
    }

    @Override
    public DossierAssistance assignerSousPartenaireDossier(Long idSousPartenaire, Long idDossier) {
        SousPartenaire sousPartenaire = sousPartenaireRepository.findById(idSousPartenaire)
                .orElseThrow(() -> new SousPartenaireNotFoundException("Sous-Partenaire avec ID " + idSousPartenaire + " non trouvé."));

        DossierAssistance dossierAssistance = dossierAssistanceRepository.findById(idDossier)
                .orElseThrow(() -> new DossierAssistanceNotFoundException("Dossier Assistance avec ID " + idDossier + " non trouvé."));

        dossierAssistance.setSousPartenaire(sousPartenaire);
        return dossierAssistanceRepository.save(dossierAssistance);
    }
}
