package com.cours.backend.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record InscriptionDTO(
        UUID id,
        UUID etudiantId,
        UUID coursId,
        String coursTitre,
        long nombreChapitres,
        int pourcentageProgression,
        LocalDateTime dateInscription
) {
}
