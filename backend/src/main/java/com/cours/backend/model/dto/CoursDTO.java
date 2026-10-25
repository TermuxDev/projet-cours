package com.cours.backend.model.dto;

import java.util.UUID;

public record CoursDTO(
        UUID id,
        String titre,
        String description,
        String categorie,
        String enseignantNom,
        long nombreInscrits,
        long nombreChapitres,
        boolean actif
) {
}
