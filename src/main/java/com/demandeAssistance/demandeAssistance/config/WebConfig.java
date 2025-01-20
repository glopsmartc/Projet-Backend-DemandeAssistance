package com.demandeAssistance.demandeAssistance.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed.origins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Permet tous les chemins
                .allowedOrigins(allowedOrigins)  // Utilise les origines configurées
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")  // Méthodes autorisées
                .allowedHeaders("*")  // Autorise tous les en-têtes
                .exposedHeaders("Authorization")  // Expose l'en-tête Authorization
                .allowCredentials(true);  // Autorise les cookies et les sessions
    }
}
