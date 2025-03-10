package com.demandeAssistance.demandeAssistance.service;

import com.demandeAssistance.demandeAssistance.dto.CreationDossierDTO;
import com.demandeAssistance.demandeAssistance.model.DossierAssistance;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface DossierAssistanceServiceInterface {

    DossierAssistance createDossier(CreationDossierDTO createDossierDTO, List<MultipartFile> documents, String token) throws Exception;

    List<DossierAssistance> getContratDossiers(Long idContrat);

    List<DossierAssistance> getAllDossiers();

    String getOffreDesciptionByContratId(Long idContrat, String token);

    List<DossierAssistance> getAllDossiersClient(String token);

    Optional<DossierAssistance> getDossierById(Long id);

    DossierAssistance updateStatutDossier(Long id, String statut);
}
