package com.cours.backend.exception;

import java.time.LocalDateTime;

public record ErreurReponse(
        LocalDateTime horodatage,
        int statut,
        String message
) {
    public ErreurReponse(int statut, String message) {
        this(LocalDateTime.now(), statut, message);
    }
}
