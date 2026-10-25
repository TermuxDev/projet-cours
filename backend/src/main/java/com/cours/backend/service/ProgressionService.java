package com.cours.backend.service;

import com.cours.backend.exception.RessourceNonTrouveeException;
import com.cours.backend.model.Utilisateur;
import com.cours.backend.model.dto.BasculeReponse;
import com.cours.backend.model.dto.ProgressionCoursDTO;
import com.cours.backend.repository.ChapitreRepository;
import com.cours.backend.repository.CoursRepository;
import com.cours.backend.repository.ProgressionRepository;
import com.cours.backend.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProgressionService {

    private final ProgressionRepository progressionRepository;
    private final ChapitreRepository chapitreRepository;
    private final CoursRepository coursRepository;
    private final UtilisateurRepository utilisateurRepository;

    public ProgressionService(ProgressionRepository progressionRepository, ChapitreRepository chapitreRepository,
                               CoursRepository coursRepository, UtilisateurRepository utilisateurRepository) {
        this.progressionRepository = progressionRepository;
        this.chapitreRepository = chapitreRepository;
        this.coursRepository = coursRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // Bascule : marque termine si non fait, retire sinon. La regle
    // "etre inscrit pour valider sa progression" reste portee par
    // sp_MarquerChapitreComplete (db/procedures.sql) cote ajout.
    @Transactional
    public BasculeReponse basculerProgression(UUID etudiantIdentifiantPublic, Long chapitreId) {
        Utilisateur etudiant = utilisateurRepository.findByIdentifiantPublic(etudiantIdentifiantPublic)
                .orElseThrow(() -> new RessourceNonTrouveeException("Utilisateur introuvable"));
        chapitreRepository.findById(chapitreId)
                .orElseThrow(() -> new RessourceNonTrouveeException("Chapitre introuvable : " + chapitreId));

        boolean dejaTermine = progressionRepository.existsByEtudiantIdAndChapitreId(etudiant.getId(), chapitreId);
        if (dejaTermine) {
            progressionRepository.deleteByEtudiantIdAndChapitreId(etudiant.getId(), chapitreId);
            return new BasculeReponse(false);
        }

        progressionRepository.marquerCompleteViaProcedure(etudiant.getId(), chapitreId);
        return new BasculeReponse(true);
    }

    @Transactional(readOnly = true)
    public ProgressionCoursDTO obtenirProgression(UUID etudiantIdentifiantPublic, UUID coursIdentifiantPublic) {
        Utilisateur etudiant = utilisateurRepository.findByIdentifiantPublic(etudiantIdentifiantPublic)
                .orElseThrow(() -> new RessourceNonTrouveeException("Utilisateur introuvable"));
        var cours = coursRepository.findByIdentifiantPublic(coursIdentifiantPublic)
                .orElseThrow(() -> new RessourceNonTrouveeException("Cours introuvable"));

        int pourcentage = progressionRepository.calculerPourcentage(etudiant.getId(), cours.getId());
        List<Long> chapitresTermines = progressionRepository
                .findByEtudiantIdAndChapitre_CoursId(etudiant.getId(), cours.getId())
                .stream()
                .map(progression -> progression.getChapitre().getId())
                .toList();

        return new ProgressionCoursDTO(pourcentage, chapitresTermines);
    }
}
