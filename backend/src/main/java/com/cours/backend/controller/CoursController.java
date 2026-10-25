package com.cours.backend.controller;

import com.cours.backend.model.dto.*;
import com.cours.backend.service.CoursService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cours")
public class CoursController {

    private final CoursService coursService;

    public CoursController(CoursService coursService) {
        this.coursService = coursService;
    }

    @GetMapping
    public List<CoursDTO> listerCoursActifs() {
        return coursService.listerCoursActifs();
    }

    // Formes d'URL distinctes de /{id} (segments supplementaires), donc
    // aucune ambiguite de routage avec Spring malgre l'ordre du fichier.
    @GetMapping("/tous")
    public List<CoursDTO> listerTousLesCours() {
        return coursService.listerTousLesCours();
    }

    @GetMapping("/enseignant/{enseignantId}")
    public List<CoursDTO> listerCoursParEnseignant(@PathVariable UUID enseignantId) {
        return coursService.listerCoursParEnseignant(enseignantId);
    }

    @GetMapping("/{id}")
    public CoursDetailDTO obtenirDetail(@PathVariable UUID id, @RequestParam(required = false) UUID visiteurId) {
        return coursService.obtenirDetail(id, visiteurId);
    }

    @GetMapping("/{id}/participants")
    public List<ParticipantDTO> listerParticipants(@PathVariable UUID id) {
        return coursService.listerParticipants(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void creerCours(@Valid @RequestBody CoursCreationRequest requete) {
        coursService.creerCours(requete);
    }

    @PutMapping("/{id}")
    public void modifierCours(@PathVariable UUID id, @Valid @RequestBody CoursModificationRequest requete) {
        coursService.modifierCours(id, requete);
    }

    @PatchMapping("/{id}/statut")
    public BasculeReponse basculerStatut(@PathVariable UUID id, @Valid @RequestBody StatutRequest requete) {
        return coursService.basculerStatut(id, requete);
    }

    @PostMapping("/{id}/chapitres")
    @ResponseStatus(HttpStatus.CREATED)
    public ChapitreDTO ajouterChapitre(@PathVariable UUID id, @Valid @RequestBody ChapitreCreationRequest requete) {
        return coursService.ajouterChapitre(id, requete);
    }

    @DeleteMapping("/{id}/chapitres/{chapitreId}")
    public void supprimerChapitre(@PathVariable UUID id, @PathVariable Long chapitreId,
                                   @RequestParam UUID enseignantId) {
        coursService.supprimerChapitre(id, chapitreId, enseignantId);
    }
}
