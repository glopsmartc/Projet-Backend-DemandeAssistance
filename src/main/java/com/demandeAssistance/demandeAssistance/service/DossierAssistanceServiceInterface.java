package com.demandeAssistance.demandeAssistance.service;

import com.demandeAssistance.demandeAssistance.dto.CreationDossierDTO;
import com.demandeAssistance.demandeAssistance.model.DossierAssistance;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DossierAssistanceServiceInterface {

    DossierAssistance createDossier(CreationDossierDTO createDossierDTO, MultipartFile constat, List<MultipartFile> documents, String token) throws IOException;

    List<DossierAssistance> getContratDossiers(Long idContrat);

    List<DossierAssistance> getAllDossiers();

    String getOffreDesciptionByContratId(Long idContrat, String token);
}
