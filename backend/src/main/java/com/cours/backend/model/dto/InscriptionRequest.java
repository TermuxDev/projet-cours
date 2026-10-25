package com.cours.backend.model.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InscriptionRequest(
        @NotNull UUID etudiantId,
        @NotNull UUID coursId
) {
}
