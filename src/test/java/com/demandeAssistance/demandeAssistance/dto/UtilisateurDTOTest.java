package com.demandeAssistance.demandeAssistance.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour UtilisateurDTO.
 * Teste les getters et setters de tous les champs pour garantir une couverture complète.
 */
class UtilisateurDTOTest {

    private UtilisateurDTO utilisateurDTO;

    @BeforeEach
    void setUp() {

        utilisateurDTO = new UtilisateurDTO();
    }

    @Test
    void testEmail() {

        String email = "test@example.com";
        utilisateurDTO.setEmail(email);
        assertEquals(email, utilisateurDTO.getEmail());


        utilisateurDTO.setEmail(null);
        assertNull(utilisateurDTO.getEmail());
    }

    @Test
    void testNom() {

        String nom = "Doe";
        utilisateurDTO.setNom(nom);
        assertEquals(nom, utilisateurDTO.getNom());


        utilisateurDTO.setNom(null);
        assertNull(utilisateurDTO.getNom());
    }

    @Test
    void testPrenom() {

        String prenom = "John";
        utilisateurDTO.setPrenom(prenom);
        assertEquals(prenom, utilisateurDTO.getPrenom());


        utilisateurDTO.setPrenom(null);
        assertNull(utilisateurDTO.getPrenom());
    }

    @Test
    void testRole() {

        String role = "CLIENT";
        utilisateurDTO.setRole(role);
        assertEquals(role, utilisateurDTO.getRole());


        utilisateurDTO.setRole(null);
        assertNull(utilisateurDTO.getRole());
    }

    @Test
    void testDefaultValues() {

        assertNull(utilisateurDTO.getEmail());
        assertNull(utilisateurDTO.getNom());
        assertNull(utilisateurDTO.getPrenom());
        assertNull(utilisateurDTO.getRole());
    }

    @Test
    void testFullInitialization() {

        utilisateurDTO.setEmail("test@example.com");
        utilisateurDTO.setNom("Doe");
        utilisateurDTO.setPrenom("John");
        utilisateurDTO.setRole("CLIENT");

        assertEquals("test@example.com", utilisateurDTO.getEmail());
        assertEquals("Doe", utilisateurDTO.getNom());
        assertEquals("John", utilisateurDTO.getPrenom());
        assertEquals("CLIENT", utilisateurDTO.getRole());
    }
}