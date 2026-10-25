package com.cours.backend.model.dto;

public record ChapitreDTO(
        Long id,
        String titre,
        int ordre,
        // null si l'appelant n'a pas acces (voir CoursService.aAccesRessources) :
        // le lien reel ne quitte jamais le serveur dans ce cas.
        String lienRessource,
        boolean aUneRessource
) {
}
