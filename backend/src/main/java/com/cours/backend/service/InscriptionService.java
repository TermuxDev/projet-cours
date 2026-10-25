package com.cours.backend.service;

import com.cours.backend.exception.InscriptionDejaExistanteException;
import com.cours.backend.exception.RessourceNonTrouveeException;
import com.cours.backend.model.Cours;
import com.cours.backend.model.Inscription;
import com.cours.backend.model.Utilisateur;
import com.cours.backend.model.dto.InscriptionDTO;
import com.cours.backend.model.dto.InscriptionRequest;
import com.cours.backend.repository.ChapitreRepository;
import com.cours.backend.repository.CoursRepository;
import com.cours.backend.repository.InscriptionRepository;
import com.cours.backend.repository.ProgressionRepository;
import com.cours.backend.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

// @Transactional de classe : necessaire pour que la lecture des
// associations paresseuses (etudiant/cours) dans versDTO() se fasse
// dans la meme session Hibernate que la requete qui les a chargees.
@Service
@Transactional
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CoursRepository coursRepository;
    private final ChapitreRepository chapitreRepository;
    private final ProgressionRepository progressionRepository;

    public InscriptionService(InscriptionRepository inscriptionRepository, UtilisateurRepository utilisateurRepository,
                               CoursRepository coursRepository, ChapitreRepository chapitreRepository,
                               ProgressionRepository progressionRepository) {
        this.inscriptionRepository = inscriptionRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.coursRepository = coursRepository;
        this.chapitreRepository = chapitreRepository;
        this.progressionRepository = progressionRepository;
    }

    public InscriptionDTO inscrire(InscriptionRequest requete) {
        Utilisateur etudiant = trouverUtilisateur(requete.etudiantId());
        Cours cours = trouverCours(requete.coursId());

        if (inscriptionRepository.existsByEtudiantIdAndCoursId(etudiant.getId(), cours.getId())) {
            throw new InscriptionDejaExistanteException("Etudiant deja inscrit a ce cours");
        }

        // La regle metier (cours actif, pas de doublon) est verifiee dans
        // sp_InscrireEtudiant au niveau de la base de donnees.
        inscriptionRepository.inscrireViaProcedure(etudiant.getId(), cours.getId());

        Inscription inscription = inscriptionRepository
                .findByEtudiantIdAndCoursId(etudiant.getId(), cours.getId())
                .orElseThrow(() -> new RessourceNonTrouveeException("Inscription introuvable apres creation"));

        return versDTO(inscription);
    }

    public List<InscriptionDTO> listerPourEtudiant(UUID etudiantIdentifiantPublic) {
        Utilisateur etudiant = trouverUtilisateur(etudiantIdentifiantPublic);
        return inscriptionRepository.findByEtudiantId(etudiant.getId()).stream()
                .map(this::versDTO)
                .toList();
    }

    private Utilisateur trouverUtilisateur(UUID identifiantPublic) {
        return utilisateurRepository.findByIdentifiantPublic(identifiantPublic)
                .orElseThrow(() -> new RessourceNonTrouveeException("Utilisateur introuvable : " + identifiantPublic));
    }

    private Cours trouverCours(UUID identifiantPublic) {
        return coursRepository.findByIdentifiantPublicAndActifTrue(identifiantPublic)
                .orElseThrow(() -> new RessourceNonTrouveeException("Cours introuvable : " + identifiantPublic));
    }

    private InscriptionDTO versDTO(Inscription inscription) {
        Long coursId = inscription.getCours().getId();
        Long etudiantId = inscription.getEtudiant().getId();
        long nombreChapitres = chapitreRepository.countByCoursId(coursId);
        int pourcentage = nombreChapitres == 0 ? 0 : progressionRepository.calculerPourcentage(etudiantId, coursId);

        return new InscriptionDTO(
                inscription.getIdentifiantPublic(),
                inscription.getEtudiant().getIdentifiantPublic(),
                inscription.getCours().getIdentifiantPublic(),
                inscription.getCours().getTitre(),
                nombreChapitres,
                pourcentage,
                inscription.getDateInscription()
        );
    }
}
