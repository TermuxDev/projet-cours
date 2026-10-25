package com.cours.backend.model.dto;

import jakarta.validation.constraints.NotBlank;

public record ChapitreCreationRequest(
        @NotBlank String titre,
        String lienRessource
) {
}
