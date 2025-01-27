package com.demandeAssistance.demandeAssistance.controller;

import com.demandeAssistance.demandeAssistance.dto.CreationDossierDTO;
import com.demandeAssistance.demandeAssistance.model.DossierAssistance;
import com.demandeAssistance.demandeAssistance.service.DossierAssistanceService;
import com.demandeAssistance.demandeAssistance.service.DossierAssistanceServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RequestMapping("/api/assistance")
@RestController
public class DossierAssistanceController {
    private final DossierAssistanceServiceInterface dossierAssistanceService;

    public DossierAssistanceController(DossierAssistanceServiceInterface dossierAssistanceService) {
        this.dossierAssistanceService = dossierAssistanceService;
    }

    @PostMapping(value="/create", consumes = {MULTIPART_FORM_DATA_VALUE, "application/json"})
    public ResponseEntity<DossierAssistance> createDossier(
            @RequestBody CreationDossierDTO createDossierDTO, @RequestParam("constat") MultipartFile constat,
            @RequestParam(value = "documents", required = false) List<MultipartFile> documents) throws IOException {

        DossierAssistance dossier = dossierAssistanceService.createDossier(createDossierDTO, constat, documents);
        return ResponseEntity.ok(dossier);
    }
}
