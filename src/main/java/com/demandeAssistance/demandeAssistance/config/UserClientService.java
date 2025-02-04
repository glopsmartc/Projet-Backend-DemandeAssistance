package com.demandeAssistance.demandeAssistance.config;

import com.demandeAssistance.demandeAssistance.controller.DossierAssistanceController;
import com.demandeAssistance.demandeAssistance.dto.MaladieChroniqueDTO;
import com.demandeAssistance.demandeAssistance.dto.UtilisateurDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserClientService {

    private final RestTemplate restTemplate;

    @Value("${utilisateur.service.url}")
    String utilisateurServiceUrl;

    @Autowired
    public UserClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    private static final Logger log = LoggerFactory.getLogger(DossierAssistanceController.class);

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

    public void updateMaladieChronique(Boolean maladieChronique, String descriptionMaladie, String token) {
        // URL de l'API du microservice gestion des contrats
        String url = utilisateurServiceUrl + "/clients/updateInfosMaladies";

        UtilisateurDTO utilisateurDTO = getAuthenticatedUser(token);

        // Création des en-têtes HTTP avec le token JWT
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");

        // Création de l'objet de demande (par exemple, un objet MaladieChroniqueDTO ou un autre DTO qui contient les informations)
        MaladieChroniqueDTO maladieDTO = new MaladieChroniqueDTO( maladieChronique, descriptionMaladie, utilisateurDTO.getEmail());

        // Création de l'entité HTTP avec les en-têtes et le corps de la requête
        HttpEntity<MaladieChroniqueDTO> entity = new HttpEntity<>(maladieDTO, headers);

        try {
            // Appel de l'API pour rajouter les infos des maladies chroniques
            ResponseEntity<Void> response = restTemplate.exchange(url,
                    HttpMethod.POST,
                    entity,
                    Void.class);

            // Vérification de la réponse HTTP
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Maladie chronique mise à jour avec succès.");
            } else {
                log.info("Erreur lors de la mise à jour de la maladie chronique. Code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.info("Erreur lors de l'appel au microservice des contrats : " + e.getMessage());
        }
    }
}

