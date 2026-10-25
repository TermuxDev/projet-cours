package com.cours.backend.model.dto;

import java.util.List;
import java.util.UUID;

public record CoursDetailDTO(
        UUID id,
        String titre,
        String description,
        String categorie,
        UUID enseignantId,
        String enseignantNom,
        boolean actif,
        long nombreInscrits,
        List<ChapitreDTO> chapitres
) {
}
