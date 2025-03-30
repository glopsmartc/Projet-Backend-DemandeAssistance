package com.demandeAssistance.demandeAssistance.bootstrap;

import com.demandeAssistance.demandeAssistance.model.SousPartenaire;
import com.demandeAssistance.demandeAssistance.model.SousPartenaireRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class SousPartenaireSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private final SousPartenaireRepository sousPartenaireRepository;

    public SousPartenaireSeeder(SousPartenaireRepository sousPartenaireRepository) {
        this.sousPartenaireRepository = sousPartenaireRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.seedSousPartenaires();
    }

    private void seedSousPartenaires() {
        this.createSousPartenaire(
                "Pierre", "Dupont", "garagiste", "+33457851141", "Europe-France",
                "5 Rue de la Mécanique, Paris", // Adresse ajoutée
                "Vidange et remplacement des filtres (huile, air, carburant, habitacle)",
                "Révision complète du véhicule",
                "Diagnostic électronique et recherche de pannes",
                "Remplacement des plaquettes et disques de frein",
                "Changement de batterie",
                "Réparation ou remplacement des amortisseurs et suspensions",
                "Réglage et géométrie du parallélisme"
        );

        this.createSousPartenaire(
                "Jisoo", "Kim", "garagiste", "+82 10-1234-5678", "Asie-Corée de sud",
                "23-4, Gangnam-daero, Séoul", // Adresse ajoutée
                "Réparation du moteur (courroie de distribution, joints, soupapes…)",
                "Remplacement de l’embrayage",
                "Réparation ou remplacement du turbo",
                "Changement de la boîte de vitesses",
                "Vidange et remplacement des filtres (huile, air, carburant, habitacle)",
                "Révision complète du véhicule",
                "Diagnostic électronique et recherche de pannes",
                "Remplacement des plaquettes et disques de frein"
        );

        this.createSousPartenaire(
                null, null, "Hôpital St. Mary de Séoul", "+8215881511", "Asie-Corée de sud",
                "Seoul Hospital Road, Séoul", // Adresse ajoutée
                "Urgences vitales (accidents de la route, arrêt cardiaque, AVC)",
                "Prise en charge des traumatismes (fractures, brûlures graves, blessures profondes)",
                "Réanimation et soins intensifs",
                "Urgences chirurgicales (appendicite aiguë, rupture d’organe)",
                "Intoxications et empoisonnements",
                "Complications respiratoires (crises d’asthme sévères, détresse respiratoire)",
                "Crises cardiaques et AVC",
                "Accouchements en urgence",
                "Urgences pédiatriques (forte fièvre, convulsions, infections graves chez l’enfant)"
        );

        this.createSousPartenaire(
                "Jean", "Leblanc", "Remorquage Paris", "+33123456789", "Europe-France",
                "12 Boulevard de la Remorque, Paris", // Adresse ajoutée
                "Remorquage 24/7 sur Paris et environs",
                "Assistance dépannage rapide en cas d'accident",
                "Transport de véhicules accidentés",
                "Remise en état de véhicule endommagé"
        );

        this.createSousPartenaire(
                "Minji", "Park", "Remorquage Séoul", "+82 2-1234-5678", "Asie-Corée du Sud",
                "77-3, Apgujeong-ro, Séoul", // Adresse ajoutée
                "Remorquage 24/7 à Séoul et alentours",
                "Dépannage de véhicule accidenté ou en panne",
                "Assistance sur route et transport de véhicules",
                "Service rapide et sécurisé pour tous types de véhicules"
        );

        this.createSousPartenaire(
                null, null, "Hôpital Ibn Sina", "+21612345678", "Afrique-Tunisie",
                "Avenue Habib Bourguiba, Tunis", // Adresse ajoutée
                "Urgences médicales et soins d'urgence",
                "Soins intensifs et réanimation",
                "Chirurgie générale et spécialisée",
                "Diagnostic et traitement des maladies chroniques",
                "Service pédiatrique et urgences pour enfants",
                "Consultations externes dans toutes les spécialités médicales"
        );
    }

    private void createSousPartenaire(String prenom, String nom, String nomEntreprise, String numTel, String zone, String adresse, String... services) {
        // Vérifie si le sous-partenaire existe déjà en base
        Optional<SousPartenaire> existing = sousPartenaireRepository.findByNumTel(numTel);

        if (existing.isPresent()) {
            return; // Ne pas insérer si un enregistrement avec le même numéro de téléphone existe
        }

        SousPartenaire sp = new SousPartenaire();
        sp.setPrenom(prenom);
        sp.setNom(nom);
        sp.setNomEntreprise(nomEntreprise);
        sp.setNumTel(numTel);
        sp.setZoneGeographique(zone);
        sp.setAdresse(adresse); // Ajout de l'adresse ici

        String servicesFormatted = Arrays.stream(services)
                .map(service -> "✔️ " + service) // Ajouter le symbole ✔️ devant chaque service
                .collect(Collectors.joining(" "));

        sp.setServicesProposes(servicesFormatted);

        sousPartenaireRepository.save(sp);
    }
}
