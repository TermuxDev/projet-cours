package com.cours.backend.service;

import com.cours.backend.exception.RessourceNonTrouveeException;
import com.cours.backend.model.Utilisateur;
import com.cours.backend.model.dto.UtilisateurDTO;
import com.cours.backend.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<UtilisateurDTO> listerTous() {
        return utilisateurRepository.findAll().stream()
                .map(this::versDTO)
                .toList();
    }

    public UtilisateurDTO obtenir(UUID identifiantPublic) {
        return utilisateurRepository.findByIdentifiantPublic(identifiantPublic)
                .map(this::versDTO)
                .orElseThrow(() -> new RessourceNonTrouveeException("Utilisateur introuvable : " + identifiantPublic));
    }

    private UtilisateurDTO versDTO(Utilisateur utilisateur) {
        return new UtilisateurDTO(
                utilisateur.getIdentifiantPublic(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getRole(),
                utilisateur.getDateCreation()
        );
    }
}
