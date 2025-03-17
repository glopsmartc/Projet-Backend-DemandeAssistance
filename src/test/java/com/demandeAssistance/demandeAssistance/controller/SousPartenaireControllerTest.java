package com.demandeAssistance.demandeAssistance.controller;

import com.demandeAssistance.demandeAssistance.model.DossierAssistance;
import com.demandeAssistance.demandeAssistance.model.SousPartenaire;
import com.demandeAssistance.demandeAssistance.service.SousPartenaireServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SousPartenaireControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SousPartenaireServiceInterface sousPartenaireService;

    @InjectMocks
    private SousPartenaireController sousPartenaireController;

    private SousPartenaire dummySousPartenaire;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(sousPartenaireController).build();

        dummySousPartenaire = new SousPartenaire();
        dummySousPartenaire.setIdSousPartenaire(1L);
        dummySousPartenaire.setNomEntreprise("Test Entreprise");
        dummySousPartenaire.setNom("Doe");
        dummySousPartenaire.setPrenom("John");
        dummySousPartenaire.setNumTel("1234567890");
        dummySousPartenaire.setZoneGeographique("Zone 1");
        dummySousPartenaire.setAdresse("123 Test Street");
        dummySousPartenaire.setServicesProposes("Service1,Service2");
    }

    @Test
    @WithMockUser(roles = "CONSEILLER")
    void ajouterSousPartenaire_ShouldReturnCreatedSousPartenaire() throws Exception {
        when(sousPartenaireService.ajouterSousPartenaire(any(SousPartenaire.class)))
                .thenReturn(dummySousPartenaire);

        mockMvc.perform(post("/api/sousPartenaires/createSousPartenaire")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomEntreprise\":\"Test Entreprise\",\"nom\":\"Doe\",\"prenom\":\"John\",\"numTel\":\"1234567890\",\"zoneGeographique\":\"Zone 1\",\"adresse\":\"123 Test Street\",\"servicesProposes\":\"Service1,Service2\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSousPartenaire").value(1))
                .andExpect(jsonPath("$.nomEntreprise").value("Test Entreprise"));
    }

    @Test
    @WithMockUser(roles = "CONSEILLER")
    void obtenirTousLesSousPartenaires_ShouldReturnListOfSousPartenaires() throws Exception {
        List<SousPartenaire> sousPartenaires = new ArrayList<>();
        sousPartenaires.add(dummySousPartenaire);

        when(sousPartenaireService.obtenirTousLesSousPartenaires()).thenReturn(sousPartenaires);

        mockMvc.perform(get("/api/sousPartenaires/allSousPartenaires"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idSousPartenaire").value(1))
                .andExpect(jsonPath("$[0].nomEntreprise").value("Test Entreprise"));
    }

    @Test
    @WithMockUser(roles = "CONSEILLER")
    void obtenirSousPartenaireParId_ShouldReturnSousPartenaire() throws Exception {
        when(sousPartenaireService.obtenirSousPartenaireParId(anyLong()))
                .thenReturn(Optional.of(dummySousPartenaire));

        mockMvc.perform(get("/api/sousPartenaires/detailsSousPartenaire/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSousPartenaire").value(1))
                .andExpect(jsonPath("$.nomEntreprise").value("Test Entreprise"));
    }


    @Test
    @WithMockUser(roles = "CONSEILLER")
    void modifierSousPartenaire_ShouldUpdateSousPartenaire() throws Exception {
        dummySousPartenaire.setNomEntreprise("Updated Entreprise");

        when(sousPartenaireService.modifierSousPartenaire(anyLong(), any(SousPartenaire.class)))
                .thenReturn(dummySousPartenaire);

        mockMvc.perform(put("/api/sousPartenaires/updateSousPartenaire/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomEntreprise\":\"Updated Entreprise\",\"nom\":\"Doe\",\"prenom\":\"John\",\"numTel\":\"1234567890\",\"zoneGeographique\":\"Zone 1\",\"adresse\":\"123 Test Street\",\"servicesProposes\":\"Service1,Service2\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomEntreprise").value("Updated Entreprise"));
    }

    @Test
    @WithMockUser(roles = "CONSEILLER")
    void supprimerSousPartenaire_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/sousPartenaires/deleteSousPartenaire/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "PARTENAIRE")
    void assignerSousPartenaireDossier_ShouldAssignDossier() throws Exception {
        when(sousPartenaireService.assignerSousPartenaireDossier(anyLong(), anyLong()))
                .thenReturn(new DossierAssistance());

        mockMvc.perform(put("/api/sousPartenaires/assigner/1/dossier/1"))
                .andExpect(status().isOk());
    }


}