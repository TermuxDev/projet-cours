package com.cours.backend.service;

import com.cours.backend.exception.RessourceNonTrouveeException;
import com.cours.backend.model.Cours;
import com.cours.backend.model.Favori;
import com.cours.backend.model.Utilisateur;
import com.cours.backend.model.dto.BasculeReponse;
import com.cours.backend.model.dto.CoursDTO;
import com.cours.backend.repository.ChapitreRepository;
import com.cours.backend.repository.CoursRepository;
import com.cours.backend.repository.FavoriRepository;
import com.cours.backend.repository.InscriptionRepository;
import com.cours.backend.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FavoriService {

    private final FavoriRepository favoriRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CoursRepository coursRepository;
    private final InscriptionRepository inscriptionRepository;
    private final ChapitreRepository chapitreRepository;

    public FavoriService(FavoriRepository favoriRepository, UtilisateurRepository utilisateurRepository,
                          CoursRepository coursRepository, InscriptionRepository inscriptionRepository,
                          ChapitreRepository chapitreRepository) {
        this.favoriRepository = favoriRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.coursRepository = coursRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.chapitreRepository = chapitreRepository;
    }

    @Transactional
    public BasculeReponse basculerFavori(UUID etudiantIdentifiantPublic, UUID coursIdentifiantPublic) {
        Utilisateur etudiant = trouverUtilisateur(etudiantIdentifiantPublic);
        Cours cours = coursRepository.findByIdentifiantPublic(coursIdentifiantPublic)
                .orElseThrow(() -> new RessourceNonTrouveeException("Cours introuvable"));

        boolean dejaAime = favoriRepository.existsByEtudiantIdAndCoursId(etudiant.getId(), cours.getId());
        if (dejaAime) {
            favoriRepository.deleteByEtudiantIdAndCoursId(etudiant.getId(), cours.getId());
            return new BasculeReponse(false);
        }

        Favori favori = new Favori();
        favori.setEtudiant(etudiant);
        favori.setCours(cours);
        favoriRepository.save(favori);
        return new BasculeReponse(true);
    }

    @Transactional(readOnly = true)
    public List<CoursDTO> listerFavoris(UUID etudiantIdentifiantPublic) {
        Utilisateur etudiant = trouverUtilisateur(etudiantIdentifiantPublic);
        return favoriRepository.findByEtudiantId(etudiant.getId()).stream()
                .map(favori -> versDTO(favori.getCours()))
                .toList();
    }

    private Utilisateur trouverUtilisateur(UUID identifiantPublic) {
        return utilisateurRepository.findByIdentifiantPublic(identifiantPublic)
                .orElseThrow(() -> new RessourceNonTrouveeException("Utilisateur introuvable : " + identifiantPublic));
    }

    private CoursDTO versDTO(Cours cours) {
        long nombreInscrits = inscriptionRepository.countByCoursId(cours.getId());
        long nombreChapitres = chapitreRepository.countByCoursId(cours.getId());
        return new CoursDTO(
                cours.getIdentifiantPublic(),
                cours.getTitre(),
                cours.getDescription(),
                cours.getCategorie(),
                cours.getEnseignant().getPrenom() + " " + cours.getEnseignant().getNom(),
                nombreInscrits,
                nombreChapitres,
                cours.isActif()
        );
    }
}
