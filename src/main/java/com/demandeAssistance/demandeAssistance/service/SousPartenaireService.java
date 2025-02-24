package com.demandeAssistance.demandeAssistance.service;

import com.demandeAssistance.demandeAssistance.model.SousPartenaire;
import com.demandeAssistance.demandeAssistance.model.SousPartenaireRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SousPartenaireService implements SousPartenaireServiceInterface {
    private final SousPartenaireRepository sousPartenaireRepository;

    public SousPartenaireService(SousPartenaireRepository sousPartenaireRepository) {
        this.sousPartenaireRepository = sousPartenaireRepository;
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
        return sousPartenaireRepository.findById(id);
    }

    @Override
    public SousPartenaire modifierSousPartenaire(Long id, SousPartenaire sousPartenaireModifie) {
        return sousPartenaireRepository.findById(id).map(sousPartenaire -> {
            sousPartenaire.setNomEntreprise(sousPartenaireModifie.getNomEntreprise());
            sousPartenaire.setNom(sousPartenaireModifie.getNom());
            sousPartenaire.setPrenom(sousPartenaireModifie.getPrenom());
            sousPartenaire.setNumTel(sousPartenaireModifie.getNumTel());
            sousPartenaire.setZoneGeographique(sousPartenaireModifie.getZoneGeographique());
            sousPartenaire.setAdresse(sousPartenaireModifie.getAdresse());
            sousPartenaire.setServicesProposes(sousPartenaireModifie.getServicesProposes());
            return sousPartenaireRepository.save(sousPartenaire);
        }).orElseThrow(() -> new RuntimeException("Sous-partenaire non trouvé !"));
    }

    @Override
    public void supprimerSousPartenaire(Long id) {
        sousPartenaireRepository.deleteById(id);
    }
}
