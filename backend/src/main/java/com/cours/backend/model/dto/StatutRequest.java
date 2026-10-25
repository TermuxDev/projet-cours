package com.cours.backend.model.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StatutRequest(
        @NotNull UUID utilisateurId
) {
}
