package com.cours.backend.model.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProgressionRequest(
        @NotNull UUID etudiantId,
        @NotNull Long chapitreId
) {
}
