package com.demandeAssistance.demandeAssistance.service;

import com.demandeAssistance.demandeAssistance.exception.DossierAssistanceNotFoundException;
import com.demandeAssistance.demandeAssistance.exception.SousPartenaireNotFoundException;
import com.demandeAssistance.demandeAssistance.model.DossierAssistance;
import com.demandeAssistance.demandeAssistance.model.DossierAssistanceRepository;
import com.demandeAssistance.demandeAssistance.model.SousPartenaire;
import com.demandeAssistance.demandeAssistance.model.SousPartenaireRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SousPartenaireServiceTest {

    @Mock
    private SousPartenaireRepository sousPartenaireRepository;

    @Mock
    private DossierAssistanceRepository dossierAssistanceRepository;

    @InjectMocks
    private SousPartenaireService sousPartenaireService;

    private SousPartenaire sousPartenaire;
    private DossierAssistance dossierAssistance;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sousPartenaireService = new SousPartenaireService(sousPartenaireRepository, dossierAssistanceRepository);


        sousPartenaire = new SousPartenaire();
        sousPartenaire.setIdSousPartenaire(1L);
        sousPartenaire.setNomEntreprise("Entreprise A");
        sousPartenaire.setNom("Dupont");
        sousPartenaire.setPrenom("Jean");
        sousPartenaire.setNumTel("0123456789");
        sousPartenaire.setZoneGeographique("Paris");
        sousPartenaire.setAdresse("123 Rue Test");
        sousPartenaire.setServicesProposes("Transport");

        dossierAssistance = new DossierAssistance();
        dossierAssistance.setIdDossier(1L);
        dossierAssistance.setDescription("Test Dossier");
    }


    @Test
    void ajouterSousPartenaire_ShouldAddSousPartenaire() {
        when(sousPartenaireRepository.save(any(SousPartenaire.class))).thenReturn(sousPartenaire);

        SousPartenaire result = sousPartenaireService.ajouterSousPartenaire(sousPartenaire);

        assertNotNull(result);
        assertEquals("Entreprise A", result.getNomEntreprise());
        verify(sousPartenaireRepository).save(sousPartenaire);
    }


    @Test
    void obtenirTousLesSousPartenaires_ShouldReturnAllSousPartenaires() {
        List<SousPartenaire> sousPartenaires = Collections.singletonList(sousPartenaire);
        when(sousPartenaireRepository.findAll()).thenReturn(sousPartenaires);

        List<SousPartenaire> result = sousPartenaireService.obtenirTousLesSousPartenaires();

        assertEquals(1, result.size());
        assertEquals(sousPartenaire, result.get(0));
        verify(sousPartenaireRepository).findAll();
    }


    @Test
    void obtenirSousPartenaireParId_ShouldReturnSousPartenaire() {
        when(sousPartenaireRepository.findById(1L)).thenReturn(Optional.of(sousPartenaire));

        Optional<SousPartenaire> result = sousPartenaireService.obtenirSousPartenaireParId(1L);

        assertTrue(result.isPresent());
        assertEquals(sousPartenaire, result.get());
        verify(sousPartenaireRepository).findById(1L);
    }

    @Test
    void obtenirSousPartenaireParId_NotFound_ShouldThrowException() {
        when(sousPartenaireRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SousPartenaireNotFoundException.class, () ->
                sousPartenaireService.obtenirSousPartenaireParId(1L));
        verify(sousPartenaireRepository).findById(1L);
    }


    @Test
    void modifierSousPartenaire_ShouldModifySousPartenaire() {
        SousPartenaire sousPartenaireModifie = new SousPartenaire();
        sousPartenaireModifie.setNomEntreprise("Entreprise B");
        sousPartenaireModifie.setNom("Martin");
        sousPartenaireModifie.setPrenom("Paul");
        sousPartenaireModifie.setNumTel("0987654321");
        sousPartenaireModifie.setZoneGeographique("Lyon");
        sousPartenaireModifie.setAdresse("456 Rue Modif");
        sousPartenaireModifie.setServicesProposes("Logistique");

        when(sousPartenaireRepository.findById(1L)).thenReturn(Optional.of(sousPartenaire));
        when(sousPartenaireRepository.save(any(SousPartenaire.class))).thenReturn(sousPartenaire);

        SousPartenaire result = sousPartenaireService.modifierSousPartenaire(1L, sousPartenaireModifie);

        assertEquals("Entreprise B", result.getNomEntreprise());
        assertEquals("Martin", result.getNom());
        assertEquals("Paul", result.getPrenom());
        assertEquals("0987654321", result.getNumTel());
        assertEquals("Lyon", result.getZoneGeographique());
        assertEquals("456 Rue Modif", result.getAdresse());
        assertEquals("Logistique", result.getServicesProposes());
        verify(sousPartenaireRepository).findById(1L);
        verify(sousPartenaireRepository).save(sousPartenaire);
    }

    @Test
    void modifierSousPartenaire_NotFound_ShouldThrowException() {
        SousPartenaire sousPartenaireModifie = new SousPartenaire();
        when(sousPartenaireRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SousPartenaireNotFoundException.class, () ->
                sousPartenaireService.modifierSousPartenaire(1L, sousPartenaireModifie));
        verify(sousPartenaireRepository).findById(1L);
    }


    @Test
    void supprimerSousPartenaire_ShouldDeleteSousPartenaire() {
        when(sousPartenaireRepository.existsById(1L)).thenReturn(true);
        doNothing().when(sousPartenaireRepository).deleteById(1L);

        sousPartenaireService.supprimerSousPartenaire(1L);

        verify(sousPartenaireRepository).existsById(1L);
        verify(sousPartenaireRepository).deleteById(1L);
    }

    @Test
    void supprimerSousPartenaire_NotFound_ShouldThrowException() {
        when(sousPartenaireRepository.existsById(1L)).thenReturn(false);

        assertThrows(SousPartenaireNotFoundException.class, () ->
                sousPartenaireService.supprimerSousPartenaire(1L));
        verify(sousPartenaireRepository).existsById(1L);
        verify(sousPartenaireRepository, never()).deleteById(anyLong());
    }


    @Test
    void assignerSousPartenaireDossier_ShouldAssignSousPartenaire() {
        dossierAssistance.setSousPartenaires(new ArrayList<>());
        when(sousPartenaireRepository.findById(1L)).thenReturn(Optional.of(sousPartenaire));
        when(dossierAssistanceRepository.findById(1L)).thenReturn(Optional.of(dossierAssistance));
        when(dossierAssistanceRepository.save(any(DossierAssistance.class))).thenReturn(dossierAssistance);

        DossierAssistance result = sousPartenaireService.assignerSousPartenaireDossier(1L, 1L);

        assertTrue(result.getSousPartenaires().contains(sousPartenaire));
        verify(sousPartenaireRepository).findById(1L);
        verify(dossierAssistanceRepository).findById(1L);
        verify(dossierAssistanceRepository).save(dossierAssistance);
    }

    @Test
    void assignerSousPartenaireDossier_NullSousPartenaires_ShouldInitializeAndAssign() {
        when(sousPartenaireRepository.findById(1L)).thenReturn(Optional.of(sousPartenaire));
        when(dossierAssistanceRepository.findById(1L)).thenReturn(Optional.of(dossierAssistance));
        when(dossierAssistanceRepository.save(any(DossierAssistance.class))).thenReturn(dossierAssistance);

        DossierAssistance result = sousPartenaireService.assignerSousPartenaireDossier(1L, 1L);

        assertNotNull(result.getSousPartenaires());
        assertTrue(result.getSousPartenaires().contains(sousPartenaire));
        verify(sousPartenaireRepository).findById(1L);
        verify(dossierAssistanceRepository).findById(1L);
        verify(dossierAssistanceRepository).save(dossierAssistance);
    }

    @Test
    void assignerSousPartenaireDossier_AlreadyAssigned_ShouldNotDuplicate() {
        dossierAssistance.setSousPartenaires(new ArrayList<>(Collections.singletonList(sousPartenaire)));
        when(sousPartenaireRepository.findById(1L)).thenReturn(Optional.of(sousPartenaire));
        when(dossierAssistanceRepository.findById(1L)).thenReturn(Optional.of(dossierAssistance));
        when(dossierAssistanceRepository.save(any(DossierAssistance.class))).thenReturn(dossierAssistance);

        DossierAssistance result = sousPartenaireService.assignerSousPartenaireDossier(1L, 1L);

        assertEquals(1, result.getSousPartenaires().size());
        assertTrue(result.getSousPartenaires().contains(sousPartenaire));
        verify(sousPartenaireRepository).findById(1L);
        verify(dossierAssistanceRepository).findById(1L);
        verify(dossierAssistanceRepository).save(dossierAssistance);
    }

    @Test
    void assignerSousPartenaireDossier_SousPartenaireNotFound_ShouldThrowException() {
        when(sousPartenaireRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SousPartenaireNotFoundException.class, () ->
                sousPartenaireService.assignerSousPartenaireDossier(1L, 1L));
        verify(sousPartenaireRepository).findById(1L);
        verify(dossierAssistanceRepository, never()).findById(anyLong());
    }

    @Test
    void assignerSousPartenaireDossier_DossierNotFound_ShouldThrowException() {
        when(sousPartenaireRepository.findById(1L)).thenReturn(Optional.of(sousPartenaire));
        when(dossierAssistanceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DossierAssistanceNotFoundException.class, () ->
                sousPartenaireService.assignerSousPartenaireDossier(1L, 1L));
        verify(sousPartenaireRepository).findById(1L);
        verify(dossierAssistanceRepository).findById(1L);
    }
}