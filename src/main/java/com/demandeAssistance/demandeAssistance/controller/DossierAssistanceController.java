package com.demandeAssistance.demandeAssistance.controller;

import com.demandeAssistance.demandeAssistance.dto.CreationDossierDTO;
import com.demandeAssistance.demandeAssistance.model.DossierAssistance;
import com.demandeAssistance.demandeAssistance.service.DossierAssistanceServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RequestMapping("/api/assistance")
@RestController
public class DossierAssistanceController {
    private final DossierAssistanceServiceInterface dossierAssistanceService;
    private static final Logger log = LoggerFactory.getLogger(DossierAssistanceController.class);

    public DossierAssistanceController(DossierAssistanceServiceInterface dossierAssistanceService) {
        this.dossierAssistanceService = dossierAssistanceService;
    }

    @PostMapping(value="/create", consumes = {MULTIPART_FORM_DATA_VALUE, "application/json"})
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<DossierAssistance> createDossier(@RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreationDossierDTO createDossierDTO, @RequestParam(value = "constat", required = false) MultipartFile constat,
            @RequestParam(value = "documents", required = false) List<MultipartFile> documents) throws IOException {

        String token = authorizationHeader.replace("Bearer ", "");

        DossierAssistance dossier = dossierAssistanceService.createDossier(createDossierDTO, constat, documents, token);
        return ResponseEntity.ok(dossier);
    }

    // Les dossiers du meme contrat
    @GetMapping("/getContratDossiers/{id}")
    @PreAuthorize("hasAnyRole('CLIENT','CONSEILLER')")
    public ResponseEntity<List<DossierAssistance>> getContratDossiers(@PathVariable Long idContrat)
    {
        List<DossierAssistance> dossierAssistances = dossierAssistanceService.getContratDossiers(idContrat);
        if (dossierAssistances.isEmpty()) {
            log.warn("No dossiers found.");
        } else {
            log.info("Found dossiers: {}", dossierAssistances);
        }
        return ResponseEntity.ok(dossierAssistances);
    }

    // Tous les dossiers dans la base de donnees
    @GetMapping("/allDossiers")
    @PreAuthorize("hasRole('CONSEILLER')")
    public ResponseEntity<List<DossierAssistance>> allDossiers() {
        log.info("Entered allDossiers method");
        List<DossierAssistance> dossiers = dossierAssistanceService.getAllDossiers();

        if (dossiers.isEmpty()) {
            log.warn("No dossiers found.");
        } else {
            log.info("Found dossiers: {}", dossiers);
        }

        return ResponseEntity.ok(dossiers);
    }

    @GetMapping("/contrat/{id}")
    @PreAuthorize("hasAnyRole('CLIENT','CONSEILLER')")
    public ResponseEntity<String> getOffreDesciptionByContratId(@PathVariable Long contratId, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        log.info("Demande de récupération du contrat avec ID: {}", contratId);

        String description = dossierAssistanceService.getOffreDesciptionByContratId(contratId, token);
        if (description == null) {
            log.warn("Aucun contrat trouvé pour l'ID: {}", contratId);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(description);
    }

}
