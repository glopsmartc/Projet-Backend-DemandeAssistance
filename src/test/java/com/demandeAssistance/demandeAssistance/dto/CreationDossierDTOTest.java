package com.demandeAssistance.demandeAssistance.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour CreationDossierDTO.
 * Teste les getters et setters de tous les champs pour garantir une couverture complète.
 */
class CreationDossierDTOTest {

    private CreationDossierDTO creationDossierDTO;

    @BeforeEach
    void setUp() {
        creationDossierDTO = new CreationDossierDTO();
    }

    @Test
    void testDescription() {

        String description = "Test Description";
        creationDossierDTO.setDescription(description);
        assertEquals(description, creationDossierDTO.getDescription());
    }

    @Test
    void testType() {

        String type = "Assistance Médicale";
        creationDossierDTO.setType(type);
        assertEquals(type, creationDossierDTO.getType());
    }

    @Test
    void testIdContrat() {

        Long idContrat = 1L;
        creationDossierDTO.setIdContrat(idContrat);
        assertEquals(idContrat, creationDossierDTO.getIdContrat());
    }

    @Test
    void testMaladieChronique() {

        creationDossierDTO.setMaladieChronique(true);
        assertTrue(creationDossierDTO.getMaladieChronique());

        creationDossierDTO.setMaladieChronique(false);
        assertFalse(creationDossierDTO.getMaladieChronique());
    }

    @Test
    void testDescriptionMaladie() {

        String descriptionMaladie = "Diabète";
        creationDossierDTO.setDescriptionMaladie(descriptionMaladie);
        assertEquals(descriptionMaladie, creationDossierDTO.getDescriptionMaladie());
    }

    @Test
    void testPositionActuelle() {

        String positionActuelle = "Paris";
        creationDossierDTO.setPositionActuelle(positionActuelle);
        assertEquals(positionActuelle, creationDossierDTO.getPositionActuelle());
    }

    @Test
    void testPriorite() {

        String priorite = "Haute";
        creationDossierDTO.setPriorite(priorite);
        assertEquals(priorite, creationDossierDTO.getPriorite());
    }

    @Test
    void testNumTel() {

        String numTel = "0123456789";
        creationDossierDTO.setNumTel(numTel);
        assertEquals(numTel, creationDossierDTO.getNumTel());
    }

    @Test
    void testEmail() {

        String email = "test@example.com";
        creationDossierDTO.setEmail(email);
        assertEquals(email, creationDossierDTO.getEmail());
    }

    @Test
    void testDefaultValues() {

        assertNull(creationDossierDTO.getDescription());
        assertNull(creationDossierDTO.getType());
        assertNull(creationDossierDTO.getIdContrat());
        assertNull(creationDossierDTO.getMaladieChronique());
        assertNull(creationDossierDTO.getDescriptionMaladie());
        assertNull(creationDossierDTO.getPositionActuelle());
        assertNull(creationDossierDTO.getPriorite());
        assertNull(creationDossierDTO.getNumTel());
        assertNull(creationDossierDTO.getEmail());
    }

    @Test
    void testFullInitialization() {

        CreationDossierDTO fullDTO = new CreationDossierDTO();
        fullDTO.setDescription("Test Description");
        fullDTO.setType("Assistance Médicale");
        fullDTO.setIdContrat(1L);
        fullDTO.setMaladieChronique(true);
        fullDTO.setDescriptionMaladie("Diabète");
        fullDTO.setPositionActuelle("Paris");
        fullDTO.setPriorite("Haute");
        fullDTO.setNumTel("0123456789");
        fullDTO.setEmail("test@example.com");

        assertEquals("Test Description", fullDTO.getDescription());
        assertEquals("Assistance Médicale", fullDTO.getType());
        assertEquals(1L, fullDTO.getIdContrat());
        assertTrue(fullDTO.getMaladieChronique());
        assertEquals("Diabète", fullDTO.getDescriptionMaladie());
        assertEquals("Paris", fullDTO.getPositionActuelle());
        assertEquals("Haute", fullDTO.getPriorite());
        assertEquals("0123456789", fullDTO.getNumTel());
        assertEquals("test@example.com", fullDTO.getEmail());
    }
}