package com.demandeAssistance.demandeAssistance.controller;

import com.demandeAssistance.demandeAssistance.model.DossierAssistance;
import com.demandeAssistance.demandeAssistance.model.SousPartenaire;
import com.demandeAssistance.demandeAssistance.service.SousPartenaireServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SousPartenaireControllerTest {

    @Mock
    private SousPartenaireServiceInterface sousPartenaireService;

    @InjectMocks
    private SousPartenaireController sousPartenaireController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAjouterSousPartenaire() {
        SousPartenaire sousPartenaire = new SousPartenaire();
        when(sousPartenaireService.ajouterSousPartenaire(any(SousPartenaire.class))).thenReturn(sousPartenaire);

        ResponseEntity<SousPartenaire> response = sousPartenaireController.ajouterSousPartenaire(sousPartenaire);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sousPartenaire, response.getBody());
        verify(sousPartenaireService, times(1)).ajouterSousPartenaire(sousPartenaire);
    }

    @Test
    void testObtenirTousLesSousPartenaires() {
        List<SousPartenaire> sousPartenaires = new ArrayList<>();
        sousPartenaires.add(new SousPartenaire());
        sousPartenaires.add(new SousPartenaire());
        when(sousPartenaireService.obtenirTousLesSousPartenaires()).thenReturn(sousPartenaires);

        ResponseEntity<List<SousPartenaire>> response = sousPartenaireController.obtenirTousLesSousPartenaires();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sousPartenaires, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(sousPartenaireService, times(1)).obtenirTousLesSousPartenaires();
    }

    @Test
    void testObtenirTousLesSousPartenaires_EmptyList() {
        when(sousPartenaireService.obtenirTousLesSousPartenaires()).thenReturn(new ArrayList<>());

        ResponseEntity<List<SousPartenaire>> response = sousPartenaireController.obtenirTousLesSousPartenaires();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(sousPartenaireService, times(1)).obtenirTousLesSousPartenaires();
    }

    @Test
    void testObtenirSousPartenaireParId() {
        Long id = 1L;
        SousPartenaire sousPartenaire = new SousPartenaire();
        when(sousPartenaireService.obtenirSousPartenaireParId(id)).thenReturn(Optional.of(sousPartenaire));

        ResponseEntity<Optional<SousPartenaire>> response = sousPartenaireController.obtenirSousPartenaireParId(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isPresent());
        assertEquals(sousPartenaire, response.getBody().get());
        verify(sousPartenaireService, times(1)).obtenirSousPartenaireParId(id);
    }

    @Test
    void testModifierSousPartenaire() {
        Long id = 1L;
        SousPartenaire sousPartenaire = new SousPartenaire();
        when(sousPartenaireService.modifierSousPartenaire(eq(id), any(SousPartenaire.class))).thenReturn(sousPartenaire);

        ResponseEntity<SousPartenaire> response = sousPartenaireController.modifierSousPartenaire(id, sousPartenaire);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sousPartenaire, response.getBody());
        verify(sousPartenaireService, times(1)).modifierSousPartenaire(id, sousPartenaire);
    }

    @Test
    void testSupprimerSousPartenaire() {
        Long id = 1L;
        doNothing().when(sousPartenaireService).supprimerSousPartenaire(id);

        ResponseEntity<Void> response = sousPartenaireController.supprimerSousPartenaire(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(sousPartenaireService, times(1)).supprimerSousPartenaire(id);
    }

    @Test
    void testAssignerSousPartenaireDossier() {
        Long idSousPartenaire = 1L;
        Long idDossier = 2L;
        DossierAssistance dossierAssistance = new DossierAssistance();
        when(sousPartenaireService.assignerSousPartenaireDossier(idSousPartenaire, idDossier)).thenReturn(dossierAssistance);

        ResponseEntity<DossierAssistance> response = sousPartenaireController.assignerSousPartenaireDossier(idSousPartenaire, idDossier);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dossierAssistance, response.getBody());
        verify(sousPartenaireService, times(1)).assignerSousPartenaireDossier(idSousPartenaire, idDossier);
    }

    @Test
    void testRemoveSousPartenaireDossier() {
        Long idSousPartenaire = 1L;
        Long idDossier = 2L;
        DossierAssistance dossierAssistance = new DossierAssistance();
        when(sousPartenaireService.removeSousPartenaireDossier(idSousPartenaire, idDossier)).thenReturn(dossierAssistance);

        ResponseEntity<DossierAssistance> response = sousPartenaireController.removeSousPartenaireDossier(idSousPartenaire, idDossier);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dossierAssistance, response.getBody());
        verify(sousPartenaireService, times(1)).removeSousPartenaireDossier(idSousPartenaire, idDossier);
    }
}