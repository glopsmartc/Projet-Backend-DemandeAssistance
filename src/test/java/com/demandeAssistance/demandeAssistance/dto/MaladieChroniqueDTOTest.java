package com.demandeAssistance.demandeAssistance.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour MaladieChroniqueDTO.
 * Teste les getters, setters et le constructeur pour garantir une couverture complète.
 */
class MaladieChroniqueDTOTest {

    private MaladieChroniqueDTO maladieChroniqueDTO;

    @BeforeEach
    void setUp() {

        maladieChroniqueDTO = new MaladieChroniqueDTO(null, null, null);
    }

    @Test
    void testConstructor() {

        MaladieChroniqueDTO dto = new MaladieChroniqueDTO(true, "Diabète", "test@example.com");

        assertTrue(dto.getMaladieChronique());
        assertEquals("Diabète", dto.getDescriptionMaladie());
        assertEquals("test@example.com", dto.getEmail());
    }

    @Test
    void testMaladieChronique() {

        maladieChroniqueDTO.setMaladieChronique(true);
        assertTrue(maladieChroniqueDTO.getMaladieChronique());

        maladieChroniqueDTO.setMaladieChronique(false);
        assertFalse(maladieChroniqueDTO.getMaladieChronique());


        maladieChroniqueDTO.setMaladieChronique(null);
        assertNull(maladieChroniqueDTO.getMaladieChronique());
    }

    @Test
    void testDescriptionMaladie() {

        String descriptionMaladie = "Hypertension";
        maladieChroniqueDTO.setDescriptionMaladie(descriptionMaladie);
        assertEquals(descriptionMaladie, maladieChroniqueDTO.getDescriptionMaladie());


        maladieChroniqueDTO.setDescriptionMaladie(null);
        assertNull(maladieChroniqueDTO.getDescriptionMaladie());
    }

    @Test
    void testEmail() {

        String email = "user@example.com";
        maladieChroniqueDTO.setEmail(email);
        assertEquals(email, maladieChroniqueDTO.getEmail());


        maladieChroniqueDTO.setEmail(null);
        assertNull(maladieChroniqueDTO.getEmail());
    }

    @Test
    void testDefaultValues() {

        assertNull(maladieChroniqueDTO.getMaladieChronique());
        assertNull(maladieChroniqueDTO.getDescriptionMaladie());
        assertNull(maladieChroniqueDTO.getEmail());
    }

    @Test
    void testFullInitializationWithSetters() {

        maladieChroniqueDTO.setMaladieChronique(true);
        maladieChroniqueDTO.setDescriptionMaladie("Asthme");
        maladieChroniqueDTO.setEmail("test@example.com");

        assertTrue(maladieChroniqueDTO.getMaladieChronique());
        assertEquals("Asthme", maladieChroniqueDTO.getDescriptionMaladie());
        assertEquals("test@example.com", maladieChroniqueDTO.getEmail());
    }
}