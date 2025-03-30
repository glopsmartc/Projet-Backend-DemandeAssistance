package com.demandeAssistance.demandeAssistance.service;

import com.demandeAssistance.demandeAssistance.config.ContratClientService;
import com.demandeAssistance.demandeAssistance.config.UserClientService;
import com.demandeAssistance.demandeAssistance.dto.CreationDossierDTO;
import com.demandeAssistance.demandeAssistance.dto.UtilisateurDTO;
import com.demandeAssistance.demandeAssistance.exception.DossierAssistanceNotFoundException;
import com.demandeAssistance.demandeAssistance.model.DossierAssistance;
import com.demandeAssistance.demandeAssistance.model.DossierAssistanceRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class DossierAssistanceService implements DossierAssistanceServiceInterface {
    private final DossierAssistanceRepository dossierAssistanceRepository;

    @Value("${file.storage.path}")
    private String storagePath;
    private final UserClientService userClientService;
    private final ContratClientService contratClientService;

    private final NotificationService notificationService;

    public DossierAssistanceService(DossierAssistanceRepository dossierAssistanceRepository, UserClientService userClientService, ContratClientService contratClientService, NotificationService notificationService) {
        this.dossierAssistanceRepository = dossierAssistanceRepository;
        this.userClientService = userClientService;
        this.contratClientService = contratClientService;
        this.notificationService = notificationService;
    }

    @Override
    public DossierAssistance createDossier(CreationDossierDTO createDossierDTO, List<MultipartFile> documents, String token) throws Exception {
        // Crée une nouvelle instance de DossierAssistance
        DossierAssistance dossier = new DossierAssistance();
        dossier.setDescription(createDossierDTO.getDescription());
        dossier.setIdContrat(createDossierDTO.getIdContrat());
        dossier.setNumTel(createDossierDTO.getNumTel());
        dossier.setTypeAssistance(createDossierDTO.getType());
        dossier.setEmail(createDossierDTO.getEmail());

        // Vérifie si la liste de documents n'est pas vide
        if (documents == null || documents.isEmpty()) {
            log.warn("Aucun document à sauvegarder pour le dossier avec le contrat ID {}", createDossierDTO.getIdContrat());
            // throw new IllegalArgumentException("La liste des documents est vide.");
            dossier.setDocuments(Collections.emptyList()); // Aucun document, on set une liste vide
        } else {
            // Enregistre les documents
            List<String> documentPaths = savePdfFiles(documents, createDossierDTO.getIdContrat(), "documents-demande-assistance", token);
            dossier.setDocuments(documentPaths);
        }

        // Ajoute les valeurs générées automatiquement
        dossier.setDateOuverture(LocalDate.now());
        dossier.setStatutDossier("En-attente");
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

        // Envoyer une notification au logisticien
        notificationService.notifyLogisticien("Un nouveau dossier d'assistance a été créé : " + dossier.getDescription());
        return dossier;
    }

    private String savePdfFile(MultipartFile file, Long id, String subFolder, String token) throws IOException {
        if (file == null || file.getOriginalFilename().isEmpty()) {
            throw new IllegalArgumentException("Fichier invalide fourni");
        }

        UtilisateurDTO utilisateurDTO = userClientService.getAuthenticatedUser(token);

        // Nom du fichier avec ID et nom original
        String fileName = utilisateurDTO.getNom()+"_"+utilisateurDTO.getPrenom() + "_" +id + "_" + file.getOriginalFilename();

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

    private List<String> savePdfFiles(List<MultipartFile> files, Long id, String subFolder, String token) throws IOException {
        List<String> filePaths = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                filePaths.add(savePdfFile(file, id, subFolder, token));
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

        try {
            String description = contratClientService.getContratById(idContrat, token);

            if (description == null || description.isEmpty()) {
                log.warn("Aucune description trouvée pour le contrat ID: {}", idContrat);
                return "Aucune description disponible"; // Ou retournez une valeur par défaut
            }

            return description;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la description du contrat ID: {}", idContrat, e);

            return "Erreur lors de la récupération de la description du contrat";
        }
    }

    @Override
    public List<DossierAssistance> getAllDossiersClient(String token) {
        List<Long> contratIds = contratClientService.getContratsIdForClient(token);
        List<DossierAssistance> allDossiers = new ArrayList<>();

        if (contratIds.isEmpty()) {
            log.warn("Aucun contrat trouvé pour ce client.");
            return Collections.emptyList();
        }

        for (Long contratId : contratIds) {
            try {
                List<DossierAssistance> dossiers = this.getContratDossiers(contratId);
                if (dossiers != null && !dossiers.isEmpty()) {
                    allDossiers.addAll(dossiers);
                }
            } catch (Exception e) {
                log.error("Erreur lors de la récupération des dossiers pour le contrat ID: {}", contratId, e);
            }
        }

        return allDossiers;
    }

    @Override
    public Optional<DossierAssistance> getDossierById(Long id) {
        return dossierAssistanceRepository.findById(id);
    }

    @Override
    public DossierAssistance updateStatutDossier(Long id, String statut) {
        return dossierAssistanceRepository.findById(id).map(dossier -> {
            if ("Clôturé".equalsIgnoreCase(statut)) {
                dossier.setDateCloture(LocalDate.now()); // Mettre à jour la date de clôture
            }
            dossier.setStatutDossier(statut); // Mettre à jour le statut
            return dossierAssistanceRepository.save(dossier);
        }).orElseThrow(() -> new RuntimeException("Dossier non trouvé avec l'ID " + id));
    }

    @Override
    public DossierAssistance assignerPartenaireDossier(Long idPartenaire, Long idDossier) {
        DossierAssistance dossierAssistance = dossierAssistanceRepository.findById(idDossier)
                .orElseThrow(() -> new DossierAssistanceNotFoundException("Dossier Assistance avec ID " + idDossier + " non trouvé."));

        dossierAssistance.setPartenaire(idPartenaire);
        return dossierAssistanceRepository.save(dossierAssistance);
    }

    @Override
    public List<DossierAssistance> getAllDossiersPartenaire(String token) {
        UtilisateurDTO utilisateurDTO = userClientService.getAuthenticatedUser(token);
        if (utilisateurDTO == null || utilisateurDTO.getId() == null) {
            throw new IllegalArgumentException("Utilisateur non trouvé ou ID invalide");
        }

        Long partenaireId = utilisateurDTO.getId();

        return dossierAssistanceRepository.findByPartenaire(partenaireId);
    }

    @Override
    @Transactional
    public DossierAssistance removePartenaireDossier(Long idDossier) {
        DossierAssistance dossier = dossierAssistanceRepository.findById(idDossier)
                .orElseThrow(() -> new RuntimeException("Dossier non trouvé avec l'ID: " + idDossier));
        
        dossier.setPartenaire(null); // Set the partenaire to null
        return dossierAssistanceRepository.save(dossier);
    }

    @Override
    public List<String> addFacturesToDossier(Long idDossier, List<MultipartFile> factureFiles, String token) throws IOException {
        // Récupérer le dossier d'assistance par son ID
        DossierAssistance dossier = dossierAssistanceRepository.findById(idDossier)
                .orElseThrow(() -> new DossierAssistanceNotFoundException("Dossier Assistance avec ID " + idDossier + " non trouvé."));

        // Vérifie si la liste de fichiers de facture est vide
        if (factureFiles == null || factureFiles.isEmpty()) {
            throw new IllegalArgumentException("Aucune facture fournie.");
        }

        // Liste pour stocker les chemins des factures enregistrées
        List<String> facturePaths = savePdfFiles(factureFiles, idDossier, "factures-demande-assistance", token);

        // Ajoute les nouveaux chemins de factures à la liste des factures du dossier
        if (dossier.getFactures() == null) {
            dossier.setFactures(new ArrayList<>());
        }
        dossier.getFactures().addAll(facturePaths);

        // Sauvegarde le dossier mis à jour
        dossierAssistanceRepository.save(dossier);

        return facturePaths;
    }

    @Override
    public DossierAssistance ajouterAction(Long idDossier, List<Map<String, Object>> actions, Double totalCost) {
        return dossierAssistanceRepository.findById(idDossier).map(dossier -> {
            // Initialise la liste des actions si elle est null
            if (dossier.getActionsRealisees() == null) {
                dossier.setActionsRealisees(new ArrayList<>());
            }
            // Ajoute chaque action avec son coût dans le format "action,cout"
            for (Map<String, Object> action : actions) {
                String name = (String) action.get("name");
                Number cost = (Number) action.get("cost");
                String actionComplete = name + "," + cost.doubleValue();
                dossier.getActionsRealisees().add(actionComplete);
            }

            // Met à jour le total des frais
            dossier.setFraisTotalDepense(totalCost);
            return dossierAssistanceRepository.save(dossier);
        }).orElseThrow(() -> new RuntimeException("Dossier non trouvé avec l'ID " + idDossier));
    }

    @Override
    public List<String> listerFactures(Long idDossier) {
        return dossierAssistanceRepository.findById(idDossier)
                .map(dossier -> {
                    List<String> factures = dossier.getFactures();
                    if (factures == null || factures.isEmpty()) {
                        log.info("Aucune facture trouvée pour le dossier {}", idDossier);
                        return Collections.<String>emptyList();
                    }
                    log.info("Listing {} factures pour le dossier {}", factures.size(), idDossier);
                    return factures;
                })
                .orElseThrow(() -> {
                    log.error("Dossier non trouvé avec l'ID {}", idDossier);
                    return new RuntimeException("Dossier non trouvé avec l'ID " + idDossier);
                });
    }

    @Override
    public List<String> listerActions(Long idDossier) {
        return dossierAssistanceRepository.findById(idDossier)
                .map(dossier -> {
                    List<String> actions = dossier.getActionsRealisees();
                    if (actions == null || actions.isEmpty()) {
                        log.info("Aucune action trouvée pour le dossier {}", idDossier);
                        return Collections.<String>emptyList();
                    }
                    log.info("Listing {} actions pour le dossier {}", actions.size(), idDossier);
                    return actions;
                })
                .orElseThrow(() -> {
                    log.error("Dossier non trouvé avec l'ID {}", idDossier);
                    return new RuntimeException("Dossier non trouvé avec l'ID " + idDossier);
                });
    }


    @Override
    public DossierAssistance supprimerAction(Long idDossier, String action) {
        return dossierAssistanceRepository.findById(idDossier).map(dossier -> {
            if (dossier.getActionsRealisees() != null) {
                // Normaliser le format de comparaison
                String actionToFind = action.contains(",") ? action : action + ",0.0";

                boolean removed = dossier.getActionsRealisees().removeIf(
                        dbAction -> {
                            // Normaliser le format de la BD (enlever les guillemets si nécessaire)
                            String normalizedDbAction = dbAction.replace("\"", "");
                            return normalizedDbAction.equals(actionToFind);
                        }
                );

                if (removed) {
                    log.info("Action supprimée: {}", actionToFind);
                    // Recalcul du total
                    double newTotal = dossier.getActionsRealisees().stream()
                            .mapToDouble(a -> {
                                String cleanAction = a.replace("\"", "");
                                String[] parts = cleanAction.split(",");
                                return parts.length > 1 ? Double.parseDouble(parts[1]) : 0;
                            })
                            .sum();
                    dossier.setFraisTotalDepense(newTotal);
                    return dossierAssistanceRepository.save(dossier);
                }
            }
            log.warn("Action non trouvée: {}", action);
            return dossier;
        }).orElseThrow(() -> new RuntimeException("Dossier non trouvé"));
    }

}
