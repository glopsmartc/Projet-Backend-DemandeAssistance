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
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.Resource;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.UrlResource;


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

    @GetMapping("/allDossiersPartenaire")
    @PreAuthorize("hasRole('PARTENAIRE')")
    public ResponseEntity<List<DossierAssistance>> allDossiersPartenaire(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");

        log.info("Entered allDossiersPartenaire method");
        List<DossierAssistance> dossiers = dossierAssistanceService.getAllDossiersPartenaire(token);

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

    @GetMapping("/dossier/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'CONSEILLER', 'LOGISTICIEN')")
    public ResponseEntity<DossierAssistance> getDossierById(@PathVariable Long id) {
        Optional<DossierAssistance> dossier = dossierAssistanceService.getDossierById(id);
        return dossier.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/updateStatut/{id}")
    @PreAuthorize("hasAnyRole('CONSEILLER', 'LOGISTICIEN')")
    public ResponseEntity<DossierAssistance> updateStatutDossier(
            @PathVariable Long id,
            @RequestParam String statut) {

        try {
            DossierAssistance dossier = dossierAssistanceService.updateStatutDossier(id, statut);
            return ResponseEntity.ok(dossier);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour du statut du dossier ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('LOGISTICIEN')")
    @PutMapping("/assigner/{idPartenaire}/dossier/{idDossier}")
    public ResponseEntity<DossierAssistance> assignerSousPartenaireDossier(
            @PathVariable(required = false) Long idPartenaire,
            @PathVariable Long idDossier) {
        DossierAssistance dossierAssistance = dossierAssistanceService.assignerPartenaireDossier(idPartenaire, idDossier);
        return ResponseEntity.ok(dossierAssistance);
    }

    @PreAuthorize("hasRole('LOGISTICIEN')")
    @PutMapping("/removePartenaire/dossier/{idDossier}")
    public ResponseEntity<DossierAssistance> removePartenaireDossier(
            @PathVariable Long idDossier) {
        DossierAssistance dossierAssistance = dossierAssistanceService.removePartenaireDossier(idDossier);
        return ResponseEntity.ok(dossierAssistance);
    }

    @GetMapping("/getFile")
    @PreAuthorize("hasAnyRole('CLIENT','LOGISTICIEN', 'PARTENAIRE')")
    public ResponseEntity<Resource> serveFile(@RequestParam String filePath) {
        try {
            Path file = Paths.get(filePath);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(file);

                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                throw new RuntimeException("File not found");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read the file!", e);
        } catch (IOException e) {
            throw new RuntimeException("Could not determine file type", e);
        }
    }

}
