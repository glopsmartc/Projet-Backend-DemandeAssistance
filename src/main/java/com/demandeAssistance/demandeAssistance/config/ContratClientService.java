package com.demandeAssistance.demandeAssistance.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

@Service
public class ContratClientService {

    private static final Logger log = LoggerFactory.getLogger(ContratClientService.class);
    private final RestTemplate restTemplate;

    @Value("${contrat.service.url}")
    String contratServiceUrl;

    @Autowired
    public ContratClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getContratById(Long id, String token) {
        // Construire l'URL de l'API pour récupérer un contrat par son ID
        String url = contratServiceUrl + "/api/contrat/offreDesciption/" + id;

        // Création des en-têtes HTTP avec le token JWT
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        // Création de l'entité HTTP avec les en-têtes
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Effectuer la requête GET vers le microservice des contrats
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Contrat récupéré avec succès pour l'ID : {}", id);
                return response.getBody();
            } else {
                log.warn("Échec de la récupération du contrat. Statut : {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du contrat avec l'ID : {}", id, e);
            return null;
        }
    }
}
