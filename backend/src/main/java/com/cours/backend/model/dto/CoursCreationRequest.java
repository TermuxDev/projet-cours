package com.cours.backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CoursCreationRequest(
        @NotNull UUID enseignantId,
        @NotBlank String titre,
        String description,
        String categorie
) {
}
