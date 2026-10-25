package com.cours.backend.config;

import com.cours.backend.model.Chapitre;
import com.cours.backend.model.Cours;
import com.cours.backend.model.RoleUtilisateur;
import com.cours.backend.model.Utilisateur;
import com.cours.backend.repository.ChapitreRepository;
import com.cours.backend.repository.CoursRepository;
import com.cours.backend.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    private static final String MOT_DE_PASSE_DEMO = "Password@2026";

    @org.springframework.context.annotation.Bean
    public CommandLineRunner initialiserDonneesDemo(UtilisateurRepository utilisateurRepository,
                                                      CoursRepository coursRepository,
                                                      ChapitreRepository chapitreRepository,
                                                      PasswordEncoder passwordEncoder) {
        return args -> {
            if (utilisateurRepository.count() > 0) {
                return;
            }

            creerUtilisateur(utilisateurRepository, passwordEncoder,
                    "Traore", "Aminata", "admin@cours.com", RoleUtilisateur.ADMINISTRATEUR);
            Utilisateur enseignant = creerUtilisateur(utilisateurRepository, passwordEncoder,
                    "Diallo", "Moussa", "enseignant1@cours.com", RoleUtilisateur.ENSEIGNANT);
            creerUtilisateur(utilisateurRepository, passwordEncoder,
                    "Kone", "Fatou", "etudiant1@cours.com", RoleUtilisateur.ETUDIANT);

            Cours coursWeb = creerCours(coursRepository, enseignant, "Developpement Web Full-Stack",
                    "Apprenez Angular et Spring Boot en construisant une vraie application.", "Informatique");
            Cours coursSql = creerCours(coursRepository, enseignant, "Bases de donnees SQL Server",
                    "Modelisation, procedures stockees et optimisation des requetes.", "Informatique");

            ajouterChapitre(chapitreRepository, coursWeb, "Introduction a Angular", 1);
            ajouterChapitre(chapitreRepository, coursWeb, "Construire une API REST avec Spring Boot", 2);
            ajouterChapitre(chapitreRepository, coursWeb, "Authentification et securite", 3);

            ajouterChapitre(chapitreRepository, coursSql, "Modelisation relationnelle", 1);
            ajouterChapitre(chapitreRepository, coursSql, "Procedures stockees et fonctions", 2);
        };
    }

    private Utilisateur creerUtilisateur(UtilisateurRepository repository, PasswordEncoder passwordEncoder,
                                          String nom, String prenom, String email, RoleUtilisateur role) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(passwordEncoder.encode(MOT_DE_PASSE_DEMO));
        utilisateur.setRole(role);
        utilisateur.setEmailVerifie(true);
        return repository.save(utilisateur);
    }

    private Cours creerCours(CoursRepository repository, Utilisateur enseignant, String titre,
                              String description, String categorie) {
        Cours cours = new Cours();
        cours.setTitre(titre);
        cours.setDescription(description);
        cours.setCategorie(categorie);
        cours.setEnseignant(enseignant);
        cours.setActif(true);
        return repository.save(cours);
    }

    private void ajouterChapitre(ChapitreRepository repository, Cours cours, String titre, int ordre) {
        Chapitre chapitre = new Chapitre();
        chapitre.setCours(cours);
        chapitre.setTitre(titre);
        chapitre.setOrdre(ordre);
        repository.save(chapitre);
    }
}
