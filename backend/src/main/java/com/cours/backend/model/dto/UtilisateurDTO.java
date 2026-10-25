package com.cours.backend.model.dto;

import com.cours.backend.model.RoleUtilisateur;

import java.time.LocalDateTime;
import java.util.UUID;

public record UtilisateurDTO(
        UUID id,
        String nom,
        String prenom,
        String email,
        RoleUtilisateur role,
        LocalDateTime dateCreation
) {
}
