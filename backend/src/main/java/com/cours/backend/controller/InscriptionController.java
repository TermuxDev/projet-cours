package com.cours.backend.controller;

import com.cours.backend.model.dto.InscriptionDTO;
import com.cours.backend.model.dto.InscriptionRequest;
import com.cours.backend.service.InscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inscriptions")
public class InscriptionController {

    private final InscriptionService inscriptionService;

    public InscriptionController(InscriptionService inscriptionService) {
        this.inscriptionService = inscriptionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InscriptionDTO inscrire(@Valid @RequestBody InscriptionRequest requete) {
        return inscriptionService.inscrire(requete);
    }

    @GetMapping("/etudiant/{etudiantId}")
    public List<InscriptionDTO> listerPourEtudiant(@PathVariable UUID etudiantId) {
        return inscriptionService.listerPourEtudiant(etudiantId);
    }
}
