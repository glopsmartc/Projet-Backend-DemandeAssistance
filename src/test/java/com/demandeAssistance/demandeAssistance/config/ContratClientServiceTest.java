package com.demandeAssistance.demandeAssistance.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ContratClientServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ContratClientService contratClientService;

    private static final String CONTRAT_SERVICE_URL = "http://localhost:8080";
    private static final String TOKEN = "test-token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        contratClientService = new ContratClientService(restTemplate);
        try {
            java.lang.reflect.Field urlField = ContratClientService.class.getDeclaredField("contratServiceUrl");
            urlField.setAccessible(true);
            urlField.set(contratClientService, CONTRAT_SERVICE_URL);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la configuration de contratServiceUrl", e);
        }
    }


    @Test
    void getContratById_Success_ShouldReturnDescription() {
        Long id = 1L;
        String url = CONTRAT_SERVICE_URL + "/api/contrat/offreDesciption/" + id;
        String description = "Contrat Description";
        ResponseEntity<String> response = new ResponseEntity<>(description, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(response);

        String result = contratClientService.getContratById(id, TOKEN);

        assertEquals(description, result);
        verify(restTemplate).exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void getContratById_NonOkStatus_ShouldReturnNull() {
        Long id = 1L;
        String url = CONTRAT_SERVICE_URL + "/api/contrat/offreDesciption/" + id;
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(response);

        String result = contratClientService.getContratById(id, TOKEN);

        assertNull(result);
        verify(restTemplate).exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void getContratById_Exception_ShouldReturnNull() {
        Long id = 1L;
        String url = CONTRAT_SERVICE_URL + "/api/contrat/offreDesciption/" + id;

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new RuntimeException("Erreur réseau"));

        String result = contratClientService.getContratById(id, TOKEN);

        assertNull(result);
        verify(restTemplate).exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        );
    }


    @Test
    void getContratsIdForClient_Success_ShouldReturnContratIds() {
        String url = CONTRAT_SERVICE_URL + "/api/contrat/user-contracts-id";
        List<Long> contratIds = Collections.singletonList(1L);
        ResponseEntity<List<Long>> response = new ResponseEntity<>(contratIds, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);

        List<Long> result = contratClientService.getContratsIdForClient(TOKEN);

        assertEquals(contratIds, result);
        verify(restTemplate).exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void getContratsIdForClient_NonOkStatus_ShouldReturnEmptyList() {
        String url = CONTRAT_SERVICE_URL + "/api/contrat/user-contracts-id";
        ResponseEntity<List<Long>> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);

        List<Long> result = contratClientService.getContratsIdForClient(TOKEN);

        assertTrue(result.isEmpty());
        verify(restTemplate).exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void getContratsIdForClient_Exception_ShouldReturnEmptyList() {
        String url = CONTRAT_SERVICE_URL + "/api/contrat/user-contracts-id";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Erreur réseau"));

        List<Long> result = contratClientService.getContratsIdForClient(TOKEN);

        assertTrue(result.isEmpty());
        verify(restTemplate).exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        );
    }
}