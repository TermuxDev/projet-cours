package com.cours.backend.controller;

import com.cours.backend.model.dto.BasculeReponse;
import com.cours.backend.model.dto.ProgressionCoursDTO;
import com.cours.backend.model.dto.ProgressionRequest;
import com.cours.backend.service.ProgressionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/progressions")
public class ProgressionController {

    private final ProgressionService progressionService;

    public ProgressionController(ProgressionService progressionService) {
        this.progressionService = progressionService;
    }

    @PostMapping
    public BasculeReponse basculer(@Valid @RequestBody ProgressionRequest requete) {
        return progressionService.basculerProgression(requete.etudiantId(), requete.chapitreId());
    }

    @GetMapping("/etudiant/{etudiantId}/cours/{coursId}")
    public ProgressionCoursDTO obtenirProgression(@PathVariable UUID etudiantId, @PathVariable UUID coursId) {
        return progressionService.obtenirProgression(etudiantId, coursId);
    }
}
