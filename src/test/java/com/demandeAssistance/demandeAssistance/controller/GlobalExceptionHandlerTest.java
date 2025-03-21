package com.demandeAssistance.demandeAssistance.controller;

import com.demandeAssistance.demandeAssistance.exception.DossierAssistanceNotFoundException;
import com.demandeAssistance.demandeAssistance.exception.SousPartenaireNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Classe de test pour GlobalExceptionHandler.
 * Utilise @WebMvcTest pour tester la gestion des exceptions globales.
 */
@WebMvcTest(controllers = {GlobalExceptionHandlerTest.TestController.class, GlobalExceptionHandler.class})
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Configuration interne pour fournir un contrôleur de test.
     */
    @Configuration
    static class TestConfig {
        @Bean
        public TestController testController() {
            return new TestController();
        }

        @Bean
        public GlobalExceptionHandler globalExceptionHandler() {
            return new GlobalExceptionHandler();
        }
    }

    /**
     * Contrôleur fictif pour déclencher les exceptions.
     */
    @RestController
    static class TestController {
        @GetMapping("/test/dossier-not-found")
        public void throwDossierNotFound() {
            throw new DossierAssistanceNotFoundException("Dossier non trouvé");
        }

        @GetMapping("/test/sous-partenaire-not-found")
        public void throwSousPartenaireNotFound() {
            throw new SousPartenaireNotFoundException("Sous-partenaire non trouvé");
        }
    }

    /**
     * Initialisation avant chaque test.
     */
    @BeforeEach
    public void setUp() {

    }

    /**
     * Test de la gestion de DossierAssistanceNotFoundException.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void testHandleDossierNotFound() throws Exception {
        mockMvc.perform(get("/test/dossier-not-found")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Dossier non trouvé"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Test de la gestion de SousPartenaireNotFoundException.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void testHandleSousPartenaireNotFound() throws Exception {
        mockMvc.perform(get("/test/sous-partenaire-not-found")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Sous-partenaire non trouvé"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}