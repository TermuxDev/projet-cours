package com.cours.backend.controller;

import com.cours.backend.model.dto.UtilisateurDTO;
import com.cours.backend.service.UtilisateurService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public List<UtilisateurDTO> listerTous() {
        return utilisateurService.listerTous();
    }

    @GetMapping("/{id}")
    public UtilisateurDTO obtenir(@PathVariable UUID id) {
        return utilisateurService.obtenir(id);
    }
}
