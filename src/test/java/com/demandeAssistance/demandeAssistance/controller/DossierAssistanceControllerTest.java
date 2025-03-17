package com.demandeAssistance.demandeAssistance.controller;

import com.demandeAssistance.demandeAssistance.dto.CreationDossierDTO;
import com.demandeAssistance.demandeAssistance.model.DossierAssistance;
import com.demandeAssistance.demandeAssistance.service.DossierAssistanceServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DossierAssistanceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DossierAssistanceServiceInterface dossierAssistanceService;

    @InjectMocks
    private DossierAssistanceController dossierAssistanceController;

    private DossierAssistance dummyDossier;

    private static final Logger log = LoggerFactory.getLogger(DossierAssistanceControllerTest.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(dossierAssistanceController).build();

        dummyDossier = new DossierAssistance();
        dummyDossier.setIdDossier(1L);
        dummyDossier.setDescription("Test dossier");
    }


    @Test
    @WithMockUser(roles = "CLIENT")
    void createDossier_ShouldReturnCreatedDossier() throws Exception {
        MockMultipartFile dossierData = new MockMultipartFile(
                "dossierData",
                "",
                "application/json",
                "{\"description\":\"Test\",\"type\":\"Type\",\"idContrat\":1}".getBytes()
        );

        MockMultipartFile pdfFile = new MockMultipartFile(
                "pdfFiles",
                "test.pdf",
                "application/pdf",
                "PDF content".getBytes()
        );

        when(dossierAssistanceService.createDossier(any(), any(), anyString()))
                .thenReturn(dummyDossier);

        mockMvc.perform(multipart("/api/assistance/create")
                        .file(dossierData)
                        .file(pdfFile)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDossier").value(1));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void createDossier_WithNoFiles_ShouldReturnCreatedDossier() throws Exception {
        MockMultipartFile dossierData = new MockMultipartFile(
                "dossierData",
                "",
                "application/json",
                "{\"description\":\"Test\",\"type\":\"Type\",\"idContrat\":1}".getBytes()
        );

        when(dossierAssistanceService.createDossier(any(), any(), anyString()))
                .thenReturn(dummyDossier);

        mockMvc.perform(multipart("/api/assistance/create")
                        .file(dossierData)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDossier").value(1));
    }


    @Test
    @WithMockUser(roles = {"CLIENT", "CONSEILLER"})
    void getContratDossiers_ShouldReturnDossiers() throws Exception {
        List<DossierAssistance> dossiers = Collections.singletonList(dummyDossier);
        when(dossierAssistanceService.getContratDossiers(anyLong())).thenReturn(dossiers);

        mockMvc.perform(get("/api/assistance/getContratDossiers")
                        .param("idContrat", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idDossier").value(1));
    }

    @Test
    @WithMockUser(roles = {"CLIENT", "CONSEILLER"})
    void getContratDossiers_ShouldReturnEmptyList() throws Exception {
        when(dossierAssistanceService.getContratDossiers(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/assistance/getContratDossiers")
                        .param("idContrat", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @WithMockUser(roles = {"CONSEILLER", "LOGISTICIEN"})
    void allDossiers_ShouldReturnAllDossiers() throws Exception {
        List<DossierAssistance> dossiers = Collections.singletonList(dummyDossier);
        when(dossierAssistanceService.getAllDossiers()).thenReturn(dossiers);

        mockMvc.perform(get("/api/assistance/allDossiers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idDossier").value(1));
    }

    @Test
    @WithMockUser(roles = {"CONSEILLER", "LOGISTICIEN"})
    void allDossiers_ShouldReturnEmptyList() throws Exception {
        when(dossierAssistanceService.getAllDossiers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/assistance/allDossiers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @WithMockUser(roles = "CLIENT")
    void allDossiersClient_ShouldReturnClientDossiers() throws Exception {
        List<DossierAssistance> dossiers = new ArrayList<>();
        dossiers.add(dummyDossier);
        when(dossierAssistanceService.getAllDossiersClient(anyString())).thenReturn(dossiers);

        mockMvc.perform(get("/api/assistance/allDossiersClient")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idDossier").value(1));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void allDossiersClient_ShouldReturnEmptyList() throws Exception {
        when(dossierAssistanceService.getAllDossiersClient(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/assistance/allDossiersClient")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @WithMockUser(roles = {"CLIENT", "CONSEILLER", "LOGISTICIEN"})
    void getDossierById_ShouldReturnDossier() throws Exception {
        when(dossierAssistanceService.getDossierById(anyLong()))
                .thenReturn(Optional.of(dummyDossier));

        mockMvc.perform(get("/api/assistance/dossier/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDossier").value(1));
    }

    @Test
    @WithMockUser(roles = {"CLIENT", "CONSEILLER", "LOGISTICIEN"})
    void getDossierById_ShouldReturnNotFound() throws Exception {
        when(dossierAssistanceService.getDossierById(anyLong()))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/assistance/dossier/1"))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(roles = {"CONSEILLER", "LOGISTICIEN"})
    void updateStatutDossier_ShouldUpdateStatus() throws Exception {
        dummyDossier.setStatutDossier("Traité");
        when(dossierAssistanceService.updateStatutDossier(anyLong(), anyString()))
                .thenReturn(dummyDossier);

        mockMvc.perform(patch("/api/assistance/updateStatut/1")
                        .param("statut", "Traité"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statutDossier").value("Traité"));
    }

    @Test
    @WithMockUser(roles = {"CONSEILLER", "LOGISTICIEN"})
    void updateStatutDossier_ShouldReturnNotFoundWhenExceptionThrown() throws Exception {
        doThrow(new RuntimeException("Dossier non trouvé")).when(dossierAssistanceService)
                .updateStatutDossier(anyLong(), anyString());

        mockMvc.perform(patch("/api/assistance/updateStatut/1")
                        .param("statut", "Traité"))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(roles = "LOGISTICIEN")
    void assignerSousPartenaireDossier_ShouldAssignPartner() throws Exception {
        dummyDossier.setPartenaire(2L);
        when(dossierAssistanceService.assignerPartenaireDossier(anyLong(), anyLong()))
                .thenReturn(dummyDossier);

        mockMvc.perform(put("/api/assistance/assigner/2/dossier/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.partenaire").value(2));
    }


    @Test
    @WithMockUser(roles = {"CLIENT", "CONSEILLER"})
    void getOffreDesciptionByContratId_ShouldReturnDescription() throws Exception {
        when(dossierAssistanceService.getOffreDesciptionByContratId(anyLong(), anyString()))
                .thenReturn("Description de l'offre");

        mockMvc.perform(get("/api/assistance/contrat")
                        .param("idContrat", "1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().string("Description de l'offre"));
    }

    @Test
    @WithMockUser(roles = {"CLIENT", "CONSEILLER"})
    void getOffreDesciptionByContratId_ShouldReturnNotFoundWhenDescriptionIsNull() throws Exception {
        when(dossierAssistanceService.getOffreDesciptionByContratId(anyLong(), anyString()))
                .thenReturn(null);

        mockMvc.perform(get("/api/assistance/contrat")
                        .param("idContrat", "1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }
}