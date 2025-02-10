package com.demandeAssistance.demandeAssistance.service;

import com.demandeAssistance.demandeAssistance.config.ContratClientService;
import com.demandeAssistance.demandeAssistance.config.UserClientService;
import com.demandeAssistance.demandeAssistance.dto.CreationDossierDTO;
import com.demandeAssistance.demandeAssistance.model.DossierAssistance;
import com.demandeAssistance.demandeAssistance.model.DossierAssistanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DossierAssistanceService implements DossierAssistanceServiceInterface {
    private final DossierAssistanceRepository dossierAssistanceRepository;

    @Value("${file.storage.path}")
    private String storagePath;
    private final UserClientService userClientService;
    private final ContratClientService contratClientService;

    public DossierAssistanceService(DossierAssistanceRepository dossierAssistanceRepository, UserClientService userClientService, ContratClientService contratClientService) {
        this.dossierAssistanceRepository = dossierAssistanceRepository;
        this.userClientService = userClientService;
        this.contratClientService = contratClientService;
    }

    @Override
    public DossierAssistance createDossier(CreationDossierDTO createDossierDTO, MultipartFile constat, List<MultipartFile> documents, String token) throws IOException {
        // Crée une nouvelle instance de DossierAssistance
        DossierAssistance dossier = new DossierAssistance();
        dossier.setDescription(createDossierDTO.getDescription());
        dossier.setIdContrat(createDossierDTO.getIdContrat());

        // Enregistre le constat
        String constatPath = savePdfFile(constat, createDossierDTO.getIdContrat(), "constats");
        dossier.setConstat(constatPath);

        // Enregistre les documents
        List<String> documentPaths = savePdfFiles(documents, createDossierDTO.getIdContrat(), "documents");
        dossier.setDocuments(documentPaths);

        // Ajoute les valeurs générées automatiquement
        dossier.setDateOuverture(LocalDate.now());
        dossier.setStatutDossier("Ouvert");
        dossier.setPositionActuelle(createDossierDTO.getPositionActuelle());
        dossier.setPriorite(createDossierDTO.getPriorite());
        dossier.setFraisTotalDepense(0.0);

        dossier = dossierAssistanceRepository.save(dossier);

        // Vérifier si l'utilisateur a une maladie chronique et mettre à jour le contrat
        if (Boolean.TRUE.equals(createDossierDTO.getMaladieChronique())) {
            log.info("Mise à jour des informations de maladie chronique pour le contrat {}", createDossierDTO.getIdContrat());
            userClientService.updateMaladieChronique(
                    createDossierDTO.getMaladieChronique(),
                    createDossierDTO.getDescriptionMaladie(),
                    token
            );
        }

        return dossier;
    }

    private String savePdfFile(MultipartFile file, Long id, String subFolder) throws IOException {
        if (file == null || file.getOriginalFilename().isEmpty()) {
            throw new IllegalArgumentException("Fichier invalide fourni");
        }

        // Nom du fichier avec ID et nom original
        String fileName = id + "_" + file.getOriginalFilename();

        // Crée le chemin du sous-dossier
        Path directory = Paths.get(storagePath, subFolder).toAbsolutePath().normalize();
        if (!directory.toFile().exists()) {
            directory.toFile().mkdirs();
        }

        // Définit le chemin complet du fichier
        Path filePath = directory.resolve(fileName);

        Path targetPath = Paths.get(storagePath).toAbsolutePath().normalize();
        if (!filePath.startsWith(targetPath)) {
            throw new IOException("Chemin invalide détecté. Traversée de chemin non autorisée.");
        }

        // Enregistre le fichier
        File destinationFile = filePath.toFile();
        file.transferTo(destinationFile);

        log.info("Fichier enregistré à l'emplacement : {}", destinationFile.getAbsolutePath());

        return destinationFile.getAbsolutePath();
    }

    private List<String> savePdfFiles(List<MultipartFile> files, Long id, String subFolder) throws IOException {
        List<String> filePaths = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                filePaths.add(savePdfFile(file, id, subFolder));
            }
        }
        return filePaths;
    }

    @Override
    public List<DossierAssistance> getContratDossiers(Long idContrat) {
        return dossierAssistanceRepository.findByIdContrat(idContrat);
    }

    @Override
    public List<DossierAssistance> getAllDossiers() {
        return (List<DossierAssistance>) dossierAssistanceRepository.findAll();
    }

    @Override
    public String getOffreDesciptionByContratId(Long idContrat, String token) {
        log.info("Récupération du contrat avec ID: {}", idContrat);
        return contratClientService.getContratById(idContrat, token);
    }
}
