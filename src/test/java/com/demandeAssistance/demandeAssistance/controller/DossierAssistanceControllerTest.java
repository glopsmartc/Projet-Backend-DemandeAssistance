package com.demandeAssistance.demandeAssistance.controller;

import com.demandeAssistance.demandeAssistance.dto.CreationDossierDTO;
import com.demandeAssistance.demandeAssistance.model.DossierAssistance;
import com.demandeAssistance.demandeAssistance.service.DossierAssistanceServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DossierAssistanceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DossierAssistanceServiceInterface dossierAssistanceService;

    @InjectMocks
    private DossierAssistanceController dossierAssistanceController;

    private DossierAssistance dummyDossier;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(dossierAssistanceController).build();

        dummyDossier = new DossierAssistance();
        dummyDossier.setIdDossier(1L);
        dummyDossier.setDescription("Test dossier");
        dummyDossier.setPartenaire(2L);
    }

    @Test
    @WithMockUser(roles = "PARTENAIRE")
    void allDossiersPartenaire_ShouldReturnDossiers() throws Exception {
        List<DossierAssistance> dossiers = Collections.singletonList(dummyDossier);
        when(dossierAssistanceService.getAllDossiersPartenaire(anyString())).thenReturn(dossiers);

        mockMvc.perform(get("/api/assistance/allDossiersPartenaire")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idDossier").value(1));
    }

    @Test
    @WithMockUser(roles = "PARTENAIRE")
    void allDossiersPartenaire_ShouldReturnEmptyList() throws Exception {
        when(dossierAssistanceService.getAllDossiersPartenaire(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/assistance/allDossiersPartenaire")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "LOGISTICIEN")
    void removePartenaireDossier_ShouldRemovePartner() throws Exception {
        when(dossierAssistanceService.removePartenaireDossier(anyLong())).thenReturn(dummyDossier);

        mockMvc.perform(put("/api/assistance/removePartenaire/dossier/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDossier").value(1));
    }

    @Test
    @WithMockUser(roles = {"CLIENT", "LOGISTICIEN", "PARTENAIRE"})
    void serveFile_ShouldReturnFile() throws Exception {
        Path tempFile = Files.createTempFile("test", ".pdf");
        Files.write(tempFile, "Test content".getBytes());

        try {
            mockMvc.perform(get("/api/assistance/getFile")
                            .param("filePath", tempFile.toString()))
                    .andExpect(status().isOk())
                    .andExpect(content().bytes("Test content".getBytes()));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }



    @Test
    @WithMockUser(roles = "CLIENT")
    void createDossier_WithBothFileTypes_ShouldHandleCorrectly() throws Exception {
        MockMultipartFile dossierData = new MockMultipartFile(
                "dossierData",
                "",
                "application/json",
                "{\"description\":\"Test\"}".getBytes()
        );

        MockMultipartFile pdfFile = new MockMultipartFile(
                "pdfFiles",
                "test.pdf",
                "application/pdf",
                "PDF content".getBytes()
        );

        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFiles",
                "test.jpg",
                "image/jpeg",
                "Image content".getBytes()
        );

        when(dossierAssistanceService.createDossier(any(), any(), anyString()))
                .thenReturn(dummyDossier);

        mockMvc.perform(multipart("/api/assistance/create")
                        .file(dossierData)
                        .file(pdfFile)
                        .file(imageFile)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }


}