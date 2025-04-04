package com.demandeAssistance.demandeAssistance.service;

import com.demandeAssistance.demandeAssistance.dto.CreationDossierDTO;
import com.demandeAssistance.demandeAssistance.model.DossierAssistance;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DossierAssistanceServiceInterface {

    DossierAssistance createDossier(CreationDossierDTO createDossierDTO, List<MultipartFile> documents, String token) throws Exception;

    List<DossierAssistance> getContratDossiers(Long idContrat);

    List<DossierAssistance> getAllDossiers();

    String getOffreDesciptionByContratId(Long idContrat, String token);

    List<DossierAssistance> getAllDossiersClient(String token);

    Optional<DossierAssistance> getDossierById(Long id);

    DossierAssistance updateStatutDossier(Long id, String statut);

    DossierAssistance assignerPartenaireDossier(Long idPartenaire, Long idDossier);

    DossierAssistance removePartenaireDossier(Long idDossier);

    List<DossierAssistance> getAllDossiersPartenaire(String token);

    List<String> addFacturesToDossier(Long idDossier, List<MultipartFile> factureFiles, String token) throws IOException;

    DossierAssistance ajouterAction(Long idDossier, List<Map<String, Object>> actions, Double totalCost);

    DossierAssistance supprimerAction(Long idDossier, String action);

    List<String> listerFactures(Long idDossier);

    List<String> listerActions(Long idDossier);
}
