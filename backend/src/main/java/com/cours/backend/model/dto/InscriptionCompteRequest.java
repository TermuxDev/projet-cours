package com.cours.backend.model.dto;

import com.cours.backend.model.RoleUtilisateur;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InscriptionCompteRequest(
        @NotBlank String nom,
        @NotBlank String prenom,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, message = "doit contenir au moins 6 caracteres") String motDePasse,
        @NotNull RoleUtilisateur role
) {
}
