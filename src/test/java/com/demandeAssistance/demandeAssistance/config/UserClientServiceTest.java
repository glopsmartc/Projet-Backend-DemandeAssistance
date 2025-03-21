package com.demandeAssistance.demandeAssistance.config;

import com.demandeAssistance.demandeAssistance.dto.MaladieChroniqueDTO;
import com.demandeAssistance.demandeAssistance.dto.UtilisateurDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserClientServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private EncryptionUtils encryptionUtils;

    @InjectMocks
    private UserClientService userClientService;

    private static final String UTILISATEUR_SERVICE_URL = "http://localhost:8081";
    private static final String TOKEN = "test-token";
    private UtilisateurDTO utilisateurDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userClientService = new UserClientService(restTemplate, encryptionUtils);
        try {
            java.lang.reflect.Field urlField = UserClientService.class.getDeclaredField("utilisateurServiceUrl");
            urlField.setAccessible(true);
            urlField.set(userClientService, UTILISATEUR_SERVICE_URL);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la configuration de utilisateurServiceUrl", e);
        }

        utilisateurDTO = new UtilisateurDTO();
        utilisateurDTO.setEmail("test@example.com");
        utilisateurDTO.setNom("Doe");
        utilisateurDTO.setPrenom("John");
        utilisateurDTO.setRole("CLIENT");
    }


    @Test
    void getAuthenticatedUser_ShouldReturnUtilisateurDTO() {
        String url = UTILISATEUR_SERVICE_URL + "/users/current-user";
        ResponseEntity<UtilisateurDTO> response = new ResponseEntity<>(utilisateurDTO, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(UtilisateurDTO.class)
        )).thenReturn(response);

        UtilisateurDTO result = userClientService.getAuthenticatedUser(TOKEN);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(restTemplate).exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(UtilisateurDTO.class)
        );
    }


    @Test
    void updateMaladieChronique_Success_ShouldUpdateSuccessfully() throws Exception {
        String url = UTILISATEUR_SERVICE_URL + "/clients/updateInfosMaladies";
        String encryptedDescription = "encryptedDiabete";
        ResponseEntity<UtilisateurDTO> userResponse = new ResponseEntity<>(utilisateurDTO, HttpStatus.OK);
        ResponseEntity<Void> updateResponse = new ResponseEntity<>(HttpStatus.OK);

        when(restTemplate.exchange(
                eq(UTILISATEUR_SERVICE_URL + "/users/current-user"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(UtilisateurDTO.class)
        )).thenReturn(userResponse);

        when(encryptionUtils.encrypt("Diabète")).thenReturn(encryptedDescription);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(updateResponse);

        userClientService.updateMaladieChronique(true, "Diabète", TOKEN);

        verify(restTemplate).exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        );
        verify(encryptionUtils).encrypt("Diabète");
    }

    @Test
    void updateMaladieChronique_NonOkStatus_ShouldLogError() throws Exception {
        String url = UTILISATEUR_SERVICE_URL + "/clients/updateInfosMaladies";
        String encryptedDescription = "encryptedDiabete";
        ResponseEntity<UtilisateurDTO> userResponse = new ResponseEntity<>(utilisateurDTO, HttpStatus.OK);
        ResponseEntity<Void> updateResponse = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        when(restTemplate.exchange(
                eq(UTILISATEUR_SERVICE_URL + "/users/current-user"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(UtilisateurDTO.class)
        )).thenReturn(userResponse);

        when(encryptionUtils.encrypt("Diabète")).thenReturn(encryptedDescription);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(updateResponse);

        userClientService.updateMaladieChronique(true, "Diabète", TOKEN);

        verify(restTemplate).exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        );
        verify(encryptionUtils).encrypt("Diabète");
    }

    @Test
    void updateMaladieChronique_Exception_ShouldLogError() throws Exception {
        String url = UTILISATEUR_SERVICE_URL + "/clients/updateInfosMaladies";
        String encryptedDescription = "encryptedDiabete";
        ResponseEntity<UtilisateurDTO> userResponse = new ResponseEntity<>(utilisateurDTO, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(UTILISATEUR_SERVICE_URL + "/users/current-user"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(UtilisateurDTO.class)
        )).thenReturn(userResponse);

        when(encryptionUtils.encrypt("Diabète")).thenReturn(encryptedDescription);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenThrow(new RuntimeException("Erreur réseau"));

        userClientService.updateMaladieChronique(true, "Diabète", TOKEN);

        verify(restTemplate).exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        );
        verify(encryptionUtils).encrypt("Diabète");
    }
}