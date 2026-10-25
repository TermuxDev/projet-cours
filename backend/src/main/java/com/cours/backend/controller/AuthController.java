package com.cours.backend.controller;

import com.cours.backend.model.dto.*;
import com.cours.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/connexion")
    public UtilisateurDTO connexion(@Valid @RequestBody ConnexionRequest requete) {
        return authService.connexion(requete);
    }

    @PostMapping("/inscription")
    @ResponseStatus(HttpStatus.CREATED)
    public void inscrire(@Valid @RequestBody InscriptionCompteRequest requete) {
        authService.inscrire(requete);
    }

    @PostMapping("/verifier-otp")
    public UtilisateurDTO verifierOtp(@Valid @RequestBody OtpRequest requete) {
        return authService.verifierOtp(requete);
    }

    @PostMapping("/renvoyer-otp")
    public void renvoyerOtp(@Valid @RequestBody EmailRequest requete) {
        authService.renvoyerOtp(requete);
    }

    @PostMapping("/mot-de-passe-oublie")
    public void demanderReinitialisation(@Valid @RequestBody EmailRequest requete) {
        authService.demanderReinitialisation(requete);
    }

    @PostMapping("/reinitialiser-mot-de-passe")
    public void reinitialiserMotDePasse(@Valid @RequestBody ReinitialisationMotDePasseRequest requete) {
        authService.reinitialiserMotDePasse(requete);
    }
}
