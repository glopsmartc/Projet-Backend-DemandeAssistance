package com.demandeAssistance.demandeAssistance.config;

import com.demandeAssistance.demandeAssistance.dto.UtilisateurDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
public class UserClientService {

    private final RestTemplate restTemplate;

    @Value("${utilisateur.service.url}")
    String utilisateurServiceUrl;

    @Autowired
    public UserClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Récupère l'utilisateur authentifié en envoyant un token JWT dans l'en-tête de la requête.
     *
     * @return L'utilisateur authentifié.
     */
    public UtilisateurDTO getAuthenticatedUser(String token) {
        // URL de l'API du microservice gestion des utilisateurs
        String url = utilisateurServiceUrl + "/users/current-user";

        // Création des en-têtes HTTP avec le token JWT
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        // Création de l'entité HTTP avec les en-têtes
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Appel de l'API pour récupérer les informations de l'utilisateur authentifié
        ResponseEntity<UtilisateurDTO> response = restTemplate.exchange(url,
                org.springframework.http.HttpMethod.GET,
                entity,
                UtilisateurDTO.class);

        // Retourne l'utilisateur authentifié
        return response.getBody();
    }
}

