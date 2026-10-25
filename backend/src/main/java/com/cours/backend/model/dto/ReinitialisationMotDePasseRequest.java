package com.cours.backend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReinitialisationMotDePasseRequest(
        @NotBlank @Email String email,
        @NotBlank String code,
        @NotBlank @Size(min = 6, message = "doit contenir au moins 6 caracteres") String nouveauMotDePasse
) {
}
