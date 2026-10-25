package com.cours.backend.service;

import com.cours.backend.exception.AccesRefuseException;
import com.cours.backend.exception.RessourceNonTrouveeException;
import com.cours.backend.model.Chapitre;
import com.cours.backend.model.Cours;
import com.cours.backend.model.Inscription;
import com.cours.backend.model.RoleUtilisateur;
import com.cours.backend.model.Utilisateur;
import com.cours.backend.model.dto.*;
import com.cours.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CoursService {

    private final CoursRepository coursRepository;
    private final ChapitreRepository chapitreRepository;
    private final InscriptionRepository inscriptionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ProgressionRepository progressionRepository;

    public CoursService(CoursRepository coursRepository, ChapitreRepository chapitreRepository,
                         InscriptionRepository inscriptionRepository, UtilisateurRepository utilisateurRepository,
                         ProgressionRepository progressionRepository) {
        this.coursRepository = coursRepository;
        this.chapitreRepository = chapitreRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.progressionRepository = progressionRepository;
    }

    public List<CoursDTO> listerCoursActifs() {
        return coursRepository.listerCoursActifs().stream()
                .map(this::versDTO)
                .toList();
    }

    // Lecture seule : laisse la session Hibernate ouverte le temps de
    // lire cours.getEnseignant() (association paresseuse) sans erreur.
    // Pas de filtre "actif" ici : le proprietaire doit pouvoir consulter
    // (et reactiver) un cours qu'il a desactive. L'inscription, elle,
    // reste bloquee sur un cours inactif (InscriptionService + procedure).
    //
    // visiteurId est optionnel (null si personne n'est connecte). Le
    // titre et l'ordre de chaque chapitre sont toujours visibles (table
    // des matieres publique, pour donner envie de s'inscrire), mais le
    // lien de ressource n'est inclus dans la reponse que si le visiteur
    // est inscrit au cours (ou en est l'enseignant, ou administrateur).
    // C'est verifie ici, cote serveur : un visiteur non autorise ne
    // recoit jamais l'URL reelle, meme en inspectant la reponse HTTP.
    @Transactional(readOnly = true)
    public CoursDetailDTO obtenirDetail(UUID identifiantPublic, UUID visiteurId) {
        Cours cours = trouverCours(identifiantPublic);
        boolean accesRessources = aAccesRessources(cours, visiteurId);

        List<ChapitreDTO> chapitres = chapitreRepository.findByCoursIdOrderByOrdreAsc(cours.getId()).stream()
                .map(chapitre -> versDTO(chapitre, accesRessources))
                .toList();

        long nombreInscrits = inscriptionRepository.countByCoursId(cours.getId());

        return new CoursDetailDTO(
                cours.getIdentifiantPublic(),
                cours.getTitre(),
                cours.getDescription(),
                cours.getCategorie(),
                cours.getEnseignant().getIdentifiantPublic(),
                cours.getEnseignant().getPrenom() + " " + cours.getEnseignant().getNom(),
                cours.isActif(),
                nombreInscrits,
                chapitres
        );
    }

    @Transactional(readOnly = true)
    public List<CoursDTO> listerCoursParEnseignant(UUID enseignantIdentifiantPublic) {
        Utilisateur enseignant = trouverUtilisateur(enseignantIdentifiantPublic);
        return coursRepository.findByEnseignantIdOrderByDateCreationDesc(enseignant.getId()).stream()
                .map(cours -> versDTOComplet(cours, enseignant))
                .toList();
    }

    // Vue d'ensemble administrateur : tous les cours, actifs ou non.
    @Transactional(readOnly = true)
    public List<CoursDTO> listerTousLesCours() {
        return coursRepository.findAllByOrderByDateCreationDesc().stream()
                .map(cours -> versDTOComplet(cours, cours.getEnseignant()))
                .toList();
    }

    @Transactional
    public void creerCours(CoursCreationRequest requete) {
        Utilisateur enseignant = trouverUtilisateur(requete.enseignantId());
        exigerEnseignant(enseignant);

        Cours cours = new Cours();
        cours.setTitre(requete.titre());
        cours.setDescription(requete.description());
        cours.setCategorie(requete.categorie());
        cours.setEnseignant(enseignant);
        cours.setActif(true);
        coursRepository.save(cours);
    }

    @Transactional
    public void modifierCours(UUID coursIdentifiantPublic, CoursModificationRequest requete) {
        Cours cours = trouverCours(coursIdentifiantPublic);
        Utilisateur demandeur = trouverUtilisateur(requete.enseignantId());
        exigerProprietaire(cours, demandeur);

        cours.setTitre(requete.titre());
        cours.setDescription(requete.description());
        cours.setCategorie(requete.categorie());
    }

    // Activer/desactiver : l'enseignant proprietaire ou un administrateur.
    @Transactional
    public BasculeReponse basculerStatut(UUID coursIdentifiantPublic, StatutRequest requete) {
        Cours cours = trouverCours(coursIdentifiantPublic);
        Utilisateur demandeur = trouverUtilisateur(requete.utilisateurId());

        boolean autorise = demandeur.getRole() == RoleUtilisateur.ADMINISTRATEUR
                || (demandeur.getRole() == RoleUtilisateur.ENSEIGNANT
                    && demandeur.getId().equals(cours.getEnseignant().getId()));
        if (!autorise) {
            throw new AccesRefuseException("Action reservee a l'enseignant proprietaire ou a un administrateur");
        }

        cours.setActif(!cours.isActif());
        return new BasculeReponse(cours.isActif());
    }

    @Transactional
    public ChapitreDTO ajouterChapitre(UUID coursIdentifiantPublic, ChapitreCreationRequest requete) {
        Cours cours = trouverCours(coursIdentifiantPublic);

        int prochainOrdre = (int) chapitreRepository.countByCoursId(cours.getId()) + 1;

        Chapitre chapitre = new Chapitre();
        chapitre.setCours(cours);
        chapitre.setTitre(requete.titre());
        chapitre.setLienRessource(requete.lienRessource());
        chapitre.setOrdre(prochainOrdre);
        chapitreRepository.save(chapitre);

        // L'enseignant qui vient de le creer a evidemment acces a sa propre ressource.
        return versDTO(chapitre, true);
    }

    @Transactional
    public void supprimerChapitre(UUID coursIdentifiantPublic, Long chapitreId, UUID enseignantIdentifiantPublic) {
        Cours cours = trouverCours(coursIdentifiantPublic);
        Utilisateur demandeur = trouverUtilisateur(enseignantIdentifiantPublic);
        exigerProprietaire(cours, demandeur);

        Chapitre chapitre = chapitreRepository.findById(chapitreId)
                .orElseThrow(() -> new RessourceNonTrouveeException("Chapitre introuvable : " + chapitreId));
        if (!chapitre.getCours().getId().equals(cours.getId())) {
            throw new RessourceNonTrouveeException("Ce chapitre n'appartient pas a ce cours");
        }

        chapitreRepository.delete(chapitre);
    }

    // Pseudonymise les noms (prenom + initiale) : on affiche qui suit le
    // cours sans exposer l'identite complete des autres utilisateurs.
    @Transactional(readOnly = true)
    public List<ParticipantDTO> listerParticipants(UUID coursIdentifiantPublic) {
        Cours cours = trouverCours(coursIdentifiantPublic);
        List<Inscription> inscriptions = inscriptionRepository.findByCoursId(cours.getId());
        long totalChapitres = chapitreRepository.countByCoursId(cours.getId());

        return inscriptions.stream()
                .map(inscription -> {
                    Utilisateur etudiant = inscription.getEtudiant();
                    String nomAffiche = etudiant.getPrenom() + " " + etudiant.getNom().charAt(0) + ".";
                    int pourcentage = totalChapitres == 0 ? 0
                            : progressionRepository.calculerPourcentage(etudiant.getId(), cours.getId());
                    return new ParticipantDTO(nomAffiche, pourcentage == 100);
                })
                .toList();
    }

    private boolean aAccesRessources(Cours cours, UUID visiteurId) {
        if (visiteurId == null) {
            return false;
        }
        return utilisateurRepository.findByIdentifiantPublic(visiteurId)
                .map(visiteur -> switch (visiteur.getRole()) {
                    case ADMINISTRATEUR -> true;
                    case ENSEIGNANT -> visiteur.getId().equals(cours.getEnseignant().getId());
                    case ETUDIANT -> inscriptionRepository.existsByEtudiantIdAndCoursId(visiteur.getId(), cours.getId());
                })
                .orElse(false);
    }

    private void exigerEnseignant(Utilisateur utilisateur) {
        if (utilisateur.getRole() != RoleUtilisateur.ENSEIGNANT) {
            throw new AccesRefuseException("Seul un enseignant peut creer un cours");
        }
    }

    private void exigerProprietaire(Cours cours, Utilisateur demandeur) {
        if (demandeur.getRole() != RoleUtilisateur.ENSEIGNANT || !demandeur.getId().equals(cours.getEnseignant().getId())) {
            throw new AccesRefuseException("Action reservee a l'enseignant proprietaire de ce cours");
        }
    }

    private Cours trouverCours(UUID identifiantPublic) {
        return coursRepository.findByIdentifiantPublic(identifiantPublic)
                .orElseThrow(() -> new RessourceNonTrouveeException("Cours introuvable : " + identifiantPublic));
    }

    private Utilisateur trouverUtilisateur(UUID identifiantPublic) {
        return utilisateurRepository.findByIdentifiantPublic(identifiantPublic)
                .orElseThrow(() -> new RessourceNonTrouveeException("Utilisateur introuvable : " + identifiantPublic));
    }

    private CoursDTO versDTO(CoursListeProjection p) {
        return new CoursDTO(p.getIdentifiantPublic(), p.getTitre(), p.getDescription(), p.getCategorie(),
                p.getEnseignantNom(), p.getNombreInscrits(), p.getNombreChapitres(), true);
    }

    private CoursDTO versDTOComplet(Cours cours, Utilisateur enseignant) {
        long nombreInscrits = inscriptionRepository.countByCoursId(cours.getId());
        long nombreChapitres = chapitreRepository.countByCoursId(cours.getId());
        return new CoursDTO(
                cours.getIdentifiantPublic(),
                cours.getTitre(),
                cours.getDescription(),
                cours.getCategorie(),
                enseignant.getPrenom() + " " + enseignant.getNom(),
                nombreInscrits,
                nombreChapitres,
                cours.isActif()
        );
    }

    private ChapitreDTO versDTO(Chapitre chapitre, boolean accesRessources) {
        boolean aUneRessource = chapitre.getLienRessource() != null && !chapitre.getLienRessource().isBlank();
        String lien = accesRessources ? chapitre.getLienRessource() : null;
        return new ChapitreDTO(chapitre.getId(), chapitre.getTitre(), chapitre.getOrdre(), lien, aUneRessource);
    }
}
