package com.demandeAssistance.demandeAssistance.controller;

import com.demandeAssistance.demandeAssistance.dto.CreationDossierDTO;
import com.demandeAssistance.demandeAssistance.model.DossierAssistance;
import com.demandeAssistance.demandeAssistance.service.DossierAssistanceServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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

    @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<DossierAssistance> createDossier(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestPart("dossierData") CreationDossierDTO createDossierDTO,
            @RequestPart(value = "pdfFiles", required = false) List<MultipartFile> pdfFiles,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) throws Exception {

        String token = authorizationHeader.replace("Bearer ", "");

        List<MultipartFile> allDocuments = new ArrayList<>();
        if (pdfFiles != null) allDocuments.addAll(pdfFiles);
        if (imageFiles != null) allDocuments.addAll(imageFiles);

        DossierAssistance dossier = dossierAssistanceService.createDossier(createDossierDTO, allDocuments, token);
        return ResponseEntity.ok(dossier);
    }


    // Les dossiers du meme contrat
    @GetMapping("/getContratDossiers")
    @PreAuthorize("hasAnyRole('CLIENT','CONSEILLER')")
    public ResponseEntity<List<DossierAssistance>> getContratDossiers(@RequestParam("idContrat") Long idContrat)
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
    @PreAuthorize("hasAnyRole('CONSEILLER', 'LOGISTICIEN')")
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

    @GetMapping("/allDossiersClient")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<DossierAssistance>> allDossiersClient(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");

        log.info("Entered allDossiersClient method");
        List<DossierAssistance> dossiers = dossierAssistanceService.getAllDossiersClient(token);

        if (dossiers.isEmpty()) {
            log.warn("No dossiers found.");
        } else {
            log.info("Found dossiers: {}", dossiers);
        }

        return ResponseEntity.ok(dossiers);
    }

    @GetMapping("/contrat")
    @PreAuthorize("hasAnyRole('CLIENT','CONSEILLER')")
    public ResponseEntity<String> getOffreDesciptionByContratId(@RequestParam("idContrat") Long contratId, @RequestHeader("Authorization") String authorizationHeader) {
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
