package com.demandeAssistance.demandeAssistance.controller;

import com.demandeAssistance.demandeAssistance.model.DossierAssistance;
import com.demandeAssistance.demandeAssistance.model.SousPartenaire;
import com.demandeAssistance.demandeAssistance.service.SousPartenaireServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sousPartenaires")
public class SousPartenaireController {
    private final SousPartenaireServiceInterface sousPartenaireService;

    public SousPartenaireController(SousPartenaireServiceInterface sousPartenaireService) {
        this.sousPartenaireService = sousPartenaireService;
    }

    @PreAuthorize("hasRole('CONSEILLER')")
    @PostMapping("/createSousPartenaire")
    public ResponseEntity<SousPartenaire> ajouterSousPartenaire(@RequestBody SousPartenaire sousPartenaire) {
        return ResponseEntity.ok(sousPartenaireService.ajouterSousPartenaire(sousPartenaire));
    }

    @PreAuthorize("hasRole('CONSEILLER') or hasRole('LOGISTICIEN') or hasRole('PARTENAIRE')")
    @GetMapping("/allSousPartenaires")
    public ResponseEntity<List<SousPartenaire>> obtenirTousLesSousPartenaires() {
        return ResponseEntity.ok(sousPartenaireService.obtenirTousLesSousPartenaires());
    }

    @PreAuthorize("hasRole('CONSEILLER') or hasRole('PARTENAIRE')")
    @GetMapping("/detailsSousPartenaire/{id}")
    public ResponseEntity<Optional<SousPartenaire>> obtenirSousPartenaireParId(@PathVariable Long id) {
        return ResponseEntity.ok(sousPartenaireService.obtenirSousPartenaireParId(id));
    }

    @PreAuthorize("hasRole('CONSEILLER')")
    @PutMapping("/updateSousPartenaire/{id}")
    public ResponseEntity<SousPartenaire> modifierSousPartenaire(@PathVariable Long id, @RequestBody SousPartenaire sousPartenaire) {
        return ResponseEntity.ok(sousPartenaireService.modifierSousPartenaire(id, sousPartenaire));
    }

    @PreAuthorize("hasRole('CONSEILLER')")
    @DeleteMapping("/deleteSousPartenaire/{id}")
    public ResponseEntity<Void> supprimerSousPartenaire(@PathVariable Long id) {
        sousPartenaireService.supprimerSousPartenaire(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('PARTENAIRE')")
    @PutMapping("/assigner/{idSousPartenaire}/dossier/{idDossier}")
    public ResponseEntity<DossierAssistance> assignerSousPartenaireDossier(
            @PathVariable Long idSousPartenaire,
            @PathVariable Long idDossier) {
        DossierAssistance dossierAssistance = sousPartenaireService.assignerSousPartenaireDossier(idSousPartenaire, idDossier);
        return ResponseEntity.ok(dossierAssistance);
    }
}
