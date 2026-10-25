package com.cours.backend.controller;

import com.cours.backend.model.dto.BasculeReponse;
import com.cours.backend.model.dto.CoursDTO;
import com.cours.backend.model.dto.FavoriRequest;
import com.cours.backend.service.FavoriService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/favoris")
public class FavoriController {

    private final FavoriService favoriService;

    public FavoriController(FavoriService favoriService) {
        this.favoriService = favoriService;
    }

    @PostMapping
    public BasculeReponse basculer(@Valid @RequestBody FavoriRequest requete) {
        return favoriService.basculerFavori(requete.etudiantId(), requete.coursId());
    }

    @GetMapping("/etudiant/{etudiantId}")
    public List<CoursDTO> listerPourEtudiant(@PathVariable UUID etudiantId) {
        return favoriService.listerFavoris(etudiantId);
    }
}
